package com.bornaapp.games.borna2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.games.borna2d.UnitConverter;
import com.bornaapp.games.borna2d.ai.GraphGenerator;
import com.bornaapp.games.borna2d.ai.GraphImp;
import com.bornaapp.games.borna2d.ai.GraphPathImp;
import com.bornaapp.games.borna2d.ai.HeuristicImp;
import com.bornaapp.games.borna2d.ai.Node;
import com.bornaapp.games.borna2d.game.Engine;
import com.bornaapp.games.borna2d.game.PlayGround;

//todo: <-------------------  a unified solution for meters/pixels etc....!!

/**
 * Created by Mehdi on 8/29/2015.
 * ...
 */
public class PathComponent extends Component {

    public GraphPathImp path = new GraphPathImp();

    public Vector2 pos = new Vector2(); //in meters
    public Vector2 vel = new Vector2(); //in meters

    private float maxLinVel = 5.00f;
    private float reachTolerance = 0.25f;

    public int currentNodeIndex = 0;
    private boolean destValid = false;
    public boolean drawDebug = false;

    //region private methods
    private Node getCurrentNode() {
        return path.get(currentNodeIndex);
    }

    private boolean reachedNode(Node node) {
        Vector2 d = getNodePosition_inMeters(node);
        d.sub(pos);
        return (d.len() < reachTolerance);
    }

    private void calculateVelocityTo(Node node, float deltaTime) {
        vel = getNodePosition_inMeters(node);
        vel.sub(pos).nor().scl(maxLinVel);
        vel.mulAdd(vel, deltaTime).limit(maxLinVel);
    }

    private Vector2 getNodePosition_inMeters(Node node) {
        Vector2 p = new Vector2();
        PlayGround playGround = Engine.getCurrentLevel().getPlayGround();//<----todo: dependent to playgound! game or engine?
        if (playGround == null)
            Engine.log.error("playGround == null");
        else {
            int i = node.getIndex();
            p.x = UnitConverter.toMeters(playGround.getWidthOfEachTileInPixels() / 2 + (i % playGround.getWidthInTiles()) * playGround.getWidthOfEachTileInPixels());
            p.y = UnitConverter.toMeters(playGround.getHeightOfEachTileInPixels() / 2 + (i / playGround.getWidthInTiles()) * playGround.getHeightOfEachTileInPixels());
        }
        return p;
    }

    private void calculatePathTo(Vector2 destination) {

        PlayGround playGround = Engine.getCurrentLevel().getPlayGround(); //<----todo: dependent to playgound! game or engine?
        if (playGround == null) {
            Engine.log.error("playGround == null");
            return;
        }
        //find path
        GraphImp graph = playGround.getGroundGraph(); // Air or Ground graph ?

        Node startNode;
        int startX = UnitConverter.toPixels(pos.x);
        int startY = UnitConverter.toPixels(pos.y);
        if (graph.isNodeAvailableAt(startX, startY))
            startNode = graph.getNodeByXY(startX, startY);
        else {
            //If start-node is not available
            cancelDestination();
            return;
        }

        Node endNode;
        int endX = (int) destination.x;
        int endY = (int) destination.y;
        if (graph.isNodeAvailableAt(endX, endY))
            endNode = graph.getNodeByXY(endX, endY);
        else {
            //If end-node is not available
            cancelDestination();
            return;
        }

        IndexedAStarPathFinder<Node> pathFinder = new IndexedAStarPathFinder<Node>(graph, false);
        path.clear();
        pathFinder.searchNodePath(startNode, endNode, new HeuristicImp(), path);
        currentNodeIndex = 0;
    }
    //endregion

    //region public methods
    public boolean isDestinationValid() {
        return destValid;
    }

    public void setDestination_inMeters(Vector2 dest_inMeters) {
        setDestination_inMeters(dest_inMeters.x, dest_inMeters.y);
    }

    public void setDestination_inMeters(float x, float y) {
        calculatePathTo(new Vector2(UnitConverter.toPixels(x), UnitConverter.toPixels(y)));
        destValid = true;
        if (path.getCount() <= 1) //No Path
            cancelDestination();
    }

    public boolean reachedDestination() {
        return (currentNodeIndex >= path.getCount());
    }

    public void cancelDestination() {
        destValid = false;
        path.clear();
        vel.x = 0;
        vel.y = 0;
    }

    public boolean reachedCurrentNode() {
        return reachedNode(getCurrentNode());
    }

    public void CalculateVelocityToCurrentNode(float deltaTime) {
        calculateVelocityTo(getCurrentNode(), deltaTime);
    }

    public void CalculateBounceVelocity(float deltaTime) {
        int destIndex = path.getCount()-1;
        vel = getNodePosition_inMeters(path.get(destIndex));
        vel.sub(pos).nor().scl(maxLinVel);
        vel.mulAdd(vel, deltaTime).limit(maxLinVel);
    }

    public void gotoNextNode() {
        if (currentNodeIndex < path.getCount() - 1)
            currentNodeIndex++;
        else
            cancelDestination();
    }

    public void setMaxLinearVelocity(float maxLinVel) {
        this.maxLinVel = maxLinVel;
    }
    //endregion
}
