/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.world.odorworld.entities;

import org.simbrain.util.UserParameter;
import org.simbrain.util.environment.ScalarSmellSource;
import org.simbrain.util.environment.SmellSource;
import org.simbrain.util.propertyeditor2.EditableObject;
import org.simbrain.workspace.Consumable;
import org.simbrain.workspace.Producible;
import org.simbrain.world.odorworld.OdorWorld;
import org.simbrain.world.odorworld.behaviors.Behavior;
import org.simbrain.world.odorworld.behaviors.StationaryBehavior;
import org.simbrain.world.odorworld.effectors.Effector;
import org.simbrain.world.odorworld.sensors.Sensor;
import org.simbrain.world.odorworld.sensors.TileSensor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent class for all Odor World objects.
 */
public class OdorWorldEntity implements EditableObject {

    // TODO:
    // Merge BasicEntity into OdorWorldEntity
    //
    // Make “Agent” a subclass with sensors, effectors, and an ability to turn and move?

    /** Support for property change events. */
    protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    //TODO: This is a first pass
    /** Type of this object.  These are mapped to images, etc. */
    public enum EntityType {
        SWISS, FLOWER, MOUSE
    }

    @UserParameter(label = "Type", order = 2)
    public EntityType entityType = EntityType.SWISS;

    /**
     * Name of this entity.
     */
    @UserParameter(label = "Name", order = 1)
    private String name;

    /**
     * Id of this entity.
     */
    private String id;

    /**
     * X Position.
     */
    protected double x;

    /**
     * Y Position.
     */
    protected double y;

    /**
     * X Velocity.
     */
    @UserParameter(label = "dx", description = "amount to move in x-direction each update", order = 5)
    protected double dx;

    /**
     * Y Velocity.
     */
    @UserParameter(label = "dy", description = "amount to move in y-direction each update", order = 5)
    protected double dy;


    /**
     * Amount to manually move forward or in cardinal directions.
     */
    @UserParameter(label = "Straigh movement", order = 10)
    protected double manualStraightMovementIncrement = 7;


    /**
     * Back reference to parent parentWorld.
     */
    private OdorWorld parentWorld;

    /**
     * Sensors.
     */
    private List<Sensor> sensors = new ArrayList<Sensor>();

    /**
     * Effectors.
     */
    private List<Effector> effectors = new ArrayList<Effector>();

    /**
     * Behavior.
     */
    protected Behavior behavior = new StationaryBehavior();

    /**
     * Smell Source (if any).
     */
    private SmellSource smellSource;

    /**
     * "Scalar" smell source, associated with the type of this object.
     */
    private ScalarSmellSource scalarSmell = new ScalarSmellSource(1);

    //TODO: Remove or at least re-implement.
    /**
     * True if a collision occurred in the last time step.
     */
    private boolean collision;

    /**
     * Enable sensors. If not the agent is "blind."
     */
    @UserParameter(label = "Enable Sensors", order = 5)
    private boolean sensorsEnabled = true;

    /**
     * Enable effectors. If not the agent is "paralyzed.
     */
    @UserParameter(label = "Enable Effectors", order = 6)
    private boolean effectorsEnabled = true;

    /**
     * If true, show sensors.
     */
    private boolean showSensors = true;

    /**
     * Things currently being said by talking entities.
     */
    private List<String> currentlyHeardPhrases = new ArrayList<String>();


    /**
     * Construct an entity.
     *
     * @param world parent world of entity
     */
    public OdorWorldEntity(OdorWorld world) {
        this.parentWorld = world;
    }

    /**
     * Construct a basic entity with a single image location.
     *
     * @param type  image location
     * @param world
     */
    public OdorWorldEntity(final EntityType type, final OdorWorld world) {
        setEntityType(type);
    }

    /**
     * Updates this OdorWorldEntity's Animation and its position based on the
     * velocity.
     */
    public void update() {

        changeSupport.firePropertyChange("updated", null, this);

        // For Backwards compatibility
        if (currentlyHeardPhrases != null) {
            currentlyHeardPhrases.clear();
        }

    }

    /**
     * Sets this OdorWorldEntity's current x position.
     *
     * @param newx
     */
    @Consumable(idMethod = "getId")
    public void setX(final double newx) {
        // System.out.println("x:" + newx);
        if (parentWorld.getWrapAround()) {
            if (newx <= 0) {
                this.x = parentWorld.getWidth() - (Math.abs(newx) % parentWorld.getWidth());
            } else if (newx > parentWorld.getWidth()) {
                this.x = newx % parentWorld.getWidth();
            } else {
                this.x = newx;
            }
        }
        changeSupport.firePropertyChange("moved", null, null);

    }

    /**
     * Sets this OdorWorldEntity's current y position.
     *
     * @param newy
     */
    @Consumable(idMethod = "getId")
    public void setY(final double newy) {
        // System.out.println("y:" + newy);
        if (parentWorld.getWrapAround()) {
            if (newy <= 0) {
                this.y = parentWorld.getHeight() - (Math.abs(newy) % parentWorld.getHeight());
            } else if (newy > parentWorld.getHeight()) {
                this.y = newy % parentWorld.getHeight();
            } else {
                this.y = newy;
            }
        }
        changeSupport.firePropertyChange("moved", null, null);
    }

    /**
     * Gets the horizontal velocity of this OdorWorldEntity in pixels per
     * millisecond.
     *
     * @return
     */
    @Producible(idMethod = "getId")
    public double getVelocityX() {
        return dx;
    }

    /**
     * Gets the vertical velocity of this OdorWorldEntity in pixels per
     * millisecond.
     *
     * @return
     */
    @Producible(idMethod = "getId")
    public double getVelocityY() {
        return dy;
    }

    /**
     * Sets the horizontal velocity of this OdorWorldEntity in pixels per
     * millisecond.
     *
     * @param dx
     */
    @Consumable(idMethod = "getId")
    public void setVelocityX(final double dx) {
        this.dx = dx;
    }

    /**
     * Sets the vertical velocity of this OdorWorldEntity in pixels per
     * millisecond.
     *
     * @param dy
     */
    @Consumable(idMethod = "getId")
    public void setVelocityY(final double dy) {
        this.dy = dy;
    }

    /**
     * Get the entity's name.
     *
     * @return entity's name.
     */
    public String getName() {
        if (name == null) {
            return id;
        } else {
            return name;
        }
    }

    /**
     * Set the entity's name.
     *
     * @param name string name for entity.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Add an effector.
     *
     * @param effector effector to add
     */
    public void addEffector(final Effector effector) {
        // if (effector.getApplicableTypes().contains(this.getClass()))...
        effectors.add(effector);
        effector.setId(parentWorld.getEffectorIDGenerator().getId());
//        parentWorld.fireEffectorAdded(effector);
    }

    /**
     * Removes an effector.
     *
     * @param effector effector to remove
     */
    public void removeEffector(final Effector effector) {
        effectors.remove(effector);
//        parentWorld.fireEffectorRemoved(effector);
    }

    /**
     * Add a sensor.
     *
     * @param sensor sensor to add
     */
    public void addSensor(final Sensor sensor) {
        // if (sensor.getApplicableTypes().contains(this.getClass()))...
        sensors.add(sensor);

        // Assign an id unless it already has one
        if (sensor.getId() == null) {
            sensor.setId(parentWorld.getSensorIDGenerator().getId());
        }

//        parentWorld.fireSensorAdded(sensor);
    }

    /**
     * Get the sensor with the specified label, or null if none found.
     * <p>
     * Some common choices: "Smell-Left", "Smell-Center", and "Smell-Right"
     *
     * @param label label to search for
     * @return the associated sensor
     */
    public final Sensor getSensor(final String label) {
        for (Sensor sensor : this.getSensors()) {
            if (sensor.getLabel().equalsIgnoreCase(label)) {
                return sensor;
            }
        }
        return null;
    }

    /**
     * Get the effector with the specified label, or null if none found.
     *
     * @param label label to search for
     * @return the associated sensor
     */
    public final Effector getEffector(final String label) {
        for (Effector effector : this.getEffectors()) {
            if (effector.getLabel().equalsIgnoreCase(label)) {
                return effector;
            }
        }
        return null;
    }

    /**
     * Removes a sensor.
     *
     * @param sensor sensor to remove
     */
    public void removeSensor(final Sensor sensor) {
        sensors.remove(sensor);
//        parentWorld.fireSensorRemoved(sensor);
    }

    /**
     * Apply impact of all effectors.
     */
    public void applyEffectors() {
        if (effectorsEnabled) {
            for (Effector effector : effectors) {
                effector.update();
            }
        }
    }

    /**
     * Update all sensors.
     */
    public void updateSensors() {
        if (sensorsEnabled) {
            for (Sensor sensor : sensors) {
                sensor.update();
            }
        }
    }

    /**
     * Add a grid of tile sensors.
     *
     * @param numTilesX number of rows in grid
     * @param numTilesY number of columns in grid
     */
    public void addTileSensors(final int numTilesX, final int numTilesY) {
        addTileSensors(numTilesX, numTilesY, 1);
    }

    /**
     * Add a grid of tile sensors, offset by some fraction of a tile's length.
     *
     * @param numTilesX number of rows in grid
     * @param numTilesY number of columns in grid
     * @param offset    offset amount in pixels
     */
    public void addTileSensors(final int numTilesX, final int numTilesY, final int offset) {
        int tileWidth = parentWorld.getWidth() / numTilesX;
        int tileHeight = parentWorld.getHeight() / numTilesY;
        for (int i = 0; i < numTilesX; i++) {
            for (int j = 0; j < numTilesY; j++) {
                addSensor(new TileSensor(this, ((i * tileWidth) + offset), ((j * tileHeight) + offset), tileWidth, tileHeight));
            }
        }
    }

    /**
     * @return the smellSource
     */
    public SmellSource getSmellSource() {
        return smellSource;
    }

    /**
     * @param smellSource the smellSource to set
     */
    public void setSmellSource(final SmellSource smellSource) {
        this.smellSource = smellSource;
        smellSource.setLocation(this.getLocation());
    }

    /**
     * @return the parentWorld
     */
    public OdorWorld getParentWorld() {
        return parentWorld;
    }

    /**
     * Returns the location of the center of this entity as a double array.
     *
     * @return center location of the entity.
     */
    @Producible(idMethod = "getId")
    public double[] getCenterLocation() {
        return new double[] {getCenterX(), getCenterY()};
    }


    //TODO: Remove
    /**
     * Returns the center x position of this entity.
     *
     * @return center x coordinate.
     */
    @Producible(idMethod = "getId")
    public double getCenterX() {
        return x;
    }

    /**
     * Returns the center y position of this entity.
     *
     * @return center y coordinate.
     */
    @Producible(idMethod = "getId")
    public double getCenterY() {
        return y;
    }

    /**
     * Set the location of this entity.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setCenterLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the location of the entity as a double array.
     *
     * @return location of the entity.
     */
    @Producible(idMethod = "getId")
    public double[] getLocation() {
        return new double[] {x, y};
    }

    /**
     * Set the location of this entity.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setLocation(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * Initialize the animation from stored image location(s).
     */
    public void postSerializationInit() {
        changeSupport = new PropertyChangeSupport(this);
        currentlyHeardPhrases = new ArrayList<String>();
        // Temporary hack because collision is turned off and some entities are
        // saved in a collided state
        this.collision = false;
    }

    /**
     * @return the sensors
     */
    public List<Sensor> getSensors() {
        return sensors;
    }

    /**
     * @param sensors the sensors to set
     */
    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    /**
     * @return the effectors
     */
    public List<Effector> getEffectors() {
        return effectors;
    }

    /**
     * @param effectors the effectors to set
     */
    public void setEffectors(List<Effector> effectors) {
        this.effectors = effectors;
    }

    /**
     * @return true if a collision occurred
     */
    public boolean hasCollided() {
        return collision;
    }

    /**
     * @param collission the collision to set
     */
    public void setHasCollided(boolean collission) {
        // if (collission) {
        // cycle.bite();
        // }
        this.collision = collission;
    }

    /**
     * @return the sensorsEnabled
     */
    public boolean isSensorsEnabled() {
        return sensorsEnabled;
    }

    /**
     * @param sensorsEnabled the sensorsEnabled to set
     */
    public void setSensorsEnabled(boolean sensorsEnabled) {
        this.sensorsEnabled = sensorsEnabled;
    }

    /**
     * @return the effectorsEnabled
     */
    public boolean isEffectorsEnabled() {
        return effectorsEnabled;
    }

    /**
     * @param effectorsEnabled the effectorsEnabled to set
     */
    public void setEffectorsEnabled(boolean effectorsEnabled) {
        this.effectorsEnabled = effectorsEnabled;
    }

    /**
     * @return the showSensors
     */
    public boolean isShowSensors() {
        return showSensors;
    }

    public void setShowSensors(boolean showSensors) {
        this.showSensors = showSensors;
    }

    // Todo: move collisions, blocks etc. to piccolo

    public boolean isBlocked() {
        if (getParentWorld().isObjectsBlockMovement()) {
            if (hasCollided()) {
                return true;
            }
        }
        return false;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    /**
     * Move the object north by the specified amount in pixels.
     *
     * @param amount amount to move by
     */
    @Consumable(idMethod = "getId")
    public void moveNorth(double amount) {
        if (!isBlocked() && (amount != 0)) {
            if (this instanceof RotatingEntity) {
                ((RotatingEntity) this).setHeading(90);
            }
            setY(y - amount);
        }
    }

    /**
     * Move the object south by the specified amount in pixels.
     *
     * @param amount amount to move by
     */
    @Consumable(idMethod = "getId")
    public void moveSouth(double amount) {
        if (!isBlocked() && (amount != 0)) {
            if (this instanceof RotatingEntity) {
                ((RotatingEntity) this).setHeading(270);
            }
            setY(y + amount);
        }
    }

    /**
     * Move the object east by the specified amount in pixels.
     *
     * @param amount amount to move by
     */
    @Consumable(idMethod = "getId")
    public void moveEast(double amount) {
        if (!isBlocked() && (amount != 0)) {
            if (this instanceof RotatingEntity) {
                ((RotatingEntity) this).setHeading(0);
            }
            setX(x +  amount);
        }
    }

    public void moveNorth() {
        moveNorth(manualStraightMovementIncrement);
    }
    public void moveSouth() {
        moveSouth(manualStraightMovementIncrement);
    }
    public void moveEast() {
        moveEast(manualStraightMovementIncrement);
    }
    public void moveWest() {
        moveWest(manualStraightMovementIncrement);
    }




    /**
     * Move the object west by the specified amount in pixels.
     *
     * @param amount amount to move by
     */
    @Consumable(idMethod = "getId")
    public void moveWest(double amount) {
        if (!isBlocked() && (amount != 0)) {
            if (this instanceof RotatingEntity) {
                ((RotatingEntity) this).setHeading(180);
            }
            setX(x -  amount);
        }
    }

    /**
     * Update this object's smell source, if any.
     */
    public void updateSmellSource() {
        if (smellSource != null) {
            smellSource.update();
        }
        scalarSmell.update();
    }

    /**
     * Add a phrase to the list of things currently being said.
     *
     * @param phrase the phrase to add
     */
    public void speakToEntity(String phrase) {
        currentlyHeardPhrases.add(phrase);
    }

    /**
     * @return the currentlyHeardPhrases
     */
    public List<String> getCurrentlyHeardPhrases() {
        return currentlyHeardPhrases;
    }

    public ScalarSmellSource getScalarSmell() {
        return scalarSmell;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    // TODO: Make finer grained. entityTypeChange?
    public void commitEditorChanges() {
        changeSupport.firePropertyChange("propertiesChanged", null, this);
    };

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Remove this entity. Assumes it's been removed from parent world already.
     */
    public void delete() {

        changeSupport.firePropertyChange("deleted", null, this);
    }

}