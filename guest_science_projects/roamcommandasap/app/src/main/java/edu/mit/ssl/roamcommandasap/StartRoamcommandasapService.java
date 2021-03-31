
/* Copyright (c) 2017, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 *
 * All rights reserved.
 *
 * The Astrobee platform is licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package edu.mit.ssl.roamcommandasap;

import org.json.JSONException;
import org.json.JSONObject;
import org.ros.node.DefaultNodeMainExecutor;

import java.net.URI;

import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.parameter.ParameterTree;

import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.android.gs.StartGuestScienceService;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class StartRoamcommandasapService extends StartGuestScienceService {

    // The API implementation
    private ApiCommandImplementation api = null;

    private RoamStatusNode roam_node=null;

    NodeMainExecutor nodeMainExecutor;
    private static final URI ROS_MASTER_URI = URI.create("http://llp:11311");
    private static final java.lang.String ROS_HOSTNAME = "hlp";

    /**
     * This function is called when the GS manager starts your apk.
     * Put all of your start up code in here.
     */
    @Override
    public void onGuestScienceStart() {
        // Get a unique instance of the Astrobee API in order to command the robot.
        api = ApiCommandImplementation.getInstance();

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(ROS_HOSTNAME);
        nodeConfiguration.setMasterUri(ROS_MASTER_URI);

        roam_node = new RoamStatusNode();  // this is the custom node used for commanding

        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(roam_node, nodeConfiguration);

        // Inform the GS Manager and the GDS that the app has been started.
        sendStarted("info");
    }

    /**
     * This function is called when the GS manager stops your apk.
     * Put all of your clean up code in here. You should also call the terminate helper function
     * at the very end of this function.
     */
    @Override
    public void onGuestScienceStop() {
        //set rosparam roamcommand to stopped
        roam_node.sendStopped();

        // Stop the API
        api.shutdownFactory();

        // Inform the GS manager and the GDS that this app stopped.
        sendStopped("info");

        // Destroy all connection with the GS Manager.
        terminate();
    }

    /**
     * This function is called when the GS manager sends a custom command to your apk.
     * Please handle your commands in this function.
     *
     * @param command
     */
    @Override
    public void onGuestScienceCustomCmd(String command) {
        /* Inform the Guest Science Manager (GSM) and the Ground Data System (GDS)
         * that this app received a command. */
        sendReceivedCustomCommand("info");

        try {
            // Transform the String command into a JSON object so we can read it.
            JSONObject jCommand = new JSONObject(command);

            // Get the name of the command we received. See commands.xml files in res folder.
            String sCommand = jCommand.getString("name");

            // JSON object that will contain the data we will send back to the GSM and GDS
            JSONObject jResult = new JSONObject();

            switch (sCommand) {
                // You may handle your commands here
                default:
                    // Inform GS Manager and GDS, then stop execution.
                    jResult.put("Summary", new JSONObject()
                        .put("Status", "ERROR")
                        .put("Message", "Unrecognized command"));
                case "command-1":
                    roam_node.sendCommand(-1);
                    break;
                case "command1":
                    roam_node.sendCommand(1);
                    break;
                case "command2":
                    roam_node.sendCommand(2);
                    break;
                case "command3":
                    roam_node.sendCommand(3);
                    break;
                case "command4":
                    roam_node.sendCommand(4);
                    break;
                case "command5":
                    roam_node.sendCommand(5);
                    break;
                case "command6":
                    roam_node.sendCommand(6);
                    break;
                case "command7":
                    roam_node.sendCommand(7);
                    break;
                case "command8":
                    roam_node.sendCommand(8);
                    break;
                case "command9":
                    roam_node.sendCommand(9);
                    break;
                case "command10":
                    roam_node.sendCommand(10);
                    break;
                case "command11":
                    roam_node.sendCommand(11);
                    break;
                case "command12":
                    roam_node.sendCommand(12);
                    break;
                case "command_set_role_chaser":
                    roam_node.setRole("chaser");
                    break;
                case "command_set_role_target":
                    roam_node.setRole("target");
                    break;
                case "command_set_role_hardware":
                    roam_node.setRole("");
                    break;
                case "command_set_ground":
                    roam_node.setGround();
                    break;
            }

            // Send data to the GS manager to be shown on the Ground Data System.
            sendData(MessageType.JSON, "data", jResult.toString());
        } catch (JSONException e) {
            // Send an error message to the GSM and GDS
            sendData(MessageType.JSON, "data", "ERROR parsing JSON");
        } catch (Exception ex) {
            // Send an error message to the GSM and GDS
            sendData(MessageType.JSON, "data", "Unrecognized ERROR");
        }
    }
}
