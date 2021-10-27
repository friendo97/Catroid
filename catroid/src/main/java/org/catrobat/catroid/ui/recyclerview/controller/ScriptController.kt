/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.recyclerview.controller

import android.util.Log
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.cast.CastManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.Brick.BrickData
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.UserDataBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.bricks.UserListBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickInterface
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserDataWrapper
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.koin.java.KoinJavaComponent
import java.io.IOException
import java.util.ArrayList

class ScriptController {
    private val lookController = LookController()
    private val soundController = SoundController()
    private val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)

    companion object {
        val TAG = ScriptController::class.java.simpleName
        const val GLOBAL_USER_VARIABLE = 0
        const val LOCAL_USER_VARIABLE = 1
        const val MULTIPLAYER_USER_VARIABLE = 2
    }

    @Throws(IOException::class, CloneNotSupportedException::class)
    fun copy(
        scriptToCopy: Script,
        destinationProject: Project?,
        destinationScene: Scene?,
        destinationSprite: Sprite?
    ): Script {

        val script: Script = scriptToCopy.clone()
        val scriptFlatBrickList: List<Brick> = ArrayList()
        script.addToFlatList(scriptFlatBrickList)

        for (brick in scriptFlatBrickList) {
            when (brick) {
                is SetLookBrick ->
                    brick.look = brick.look?.let { look ->
                        lookController.findOrCopy(look, destinationScene, destinationSprite)
                    }

                is WhenBackgroundChangesBrick ->
                    brick.look = brick.look?.let { look ->
                        lookController.findOrCopy(look, destinationScene, destinationSprite)
                    }

                is PlaySoundBrick ->
                    brick.sound = brick.sound?.let { sound ->
                        soundController.findOrCopy(sound, destinationScene, destinationSprite)
                    }

                is PlaySoundAndWaitBrick ->
                    brick.sound = brick.sound?.let { sound ->
                        soundController.findOrCopy(sound, destinationScene, destinationSprite)
                    }

                is UserVariableBrickInterface ->
                    brick.userVariable?.let {
                        updateUserVariable(brick, destinationProject, destinationSprite)
                    }

                is UserListBrick ->
                    brick.userList?.let {
                        updateUserList(brick, destinationProject, destinationSprite)
                    }

                is UserDataBrick ->
                    updateUserData(brick, destinationProject, destinationSprite)
            }
        }
        return script
    }

    @Throws(CloneNotSupportedException::class)
    fun pack(groupName: String?, bricksToPack: List<Brick>?) {
        val scriptsToPack: MutableList<Script> = ArrayList()
        val userDefinedBrickListToPack: MutableList<UserDefinedBrick> = ArrayList()
        bricksToPack?.forEach() { brick ->
            if (brick is ScriptBrick) {
                if (brick is UserDefinedReceiverBrick) {
                    val userDefinedBrick = brick.userDefinedBrick
                    userDefinedBrickListToPack.add(userDefinedBrick.clone() as UserDefinedBrick)
                }
                val scriptToPack = brick.getScript()
                scriptsToPack.add(scriptToPack.clone())
            }
            checkForUserData(brick, groupName)
        }

        BackpackListManager.getInstance().addUserDefinedBrickToBackPack(groupName, userDefinedBrickListToPack)
        BackpackListManager.getInstance().addScriptToBackPack(groupName, scriptsToPack)
        BackpackListManager.getInstance().saveBackpack()
    }

    private fun checkForUserData(brick: Brick, groupName: String?) {
        if (brick is UserVariableBrickInterface) {
            createInitialHashmap(BackpackListManager.getInstance().backpack.backpackedUserVariables, groupName)

            when {
                projectManager.currentProject.userVariables.contains(brick.userVariable) ->
                    addUserDataToBackpack(brick, groupName, GLOBAL_USER_VARIABLE)
                projectManager.currentSprite.userVariables.contains(brick.userVariable) ->
                    addUserDataToBackpack(brick, groupName, LOCAL_USER_VARIABLE)
                projectManager.currentProject.multiplayerVariables.contains(brick.userVariable) ->
                    addUserDataToBackpack(brick, groupName, MULTIPLAYER_USER_VARIABLE)
            }
        }

        if (brick is UserListBrick) {
            createInitialHashmap(BackpackListManager.getInstance().backpack.backpackedUserLists, groupName)

            when {
                projectManager.currentProject.userLists.contains(brick.userList) ->
                    addUserDataToBackpack(brick, groupName, GLOBAL_USER_VARIABLE)
                projectManager.currentSprite.userLists.contains(brick.userList) ->
                    addUserDataToBackpack(brick, groupName, LOCAL_USER_VARIABLE)
            }
        }
    }

    private fun createInitialHashmap(map: HashMap<String?, HashMap<String, Int>>?, groupName: String?) {
        if (map?.containsKey(groupName) == false) {
            map[groupName] = HashMap()
        }
    }

    private fun <T> addUserDataToBackpack(brick: T, groupName: String?, type: Int) {
        var map: HashMap<String, Int>? = HashMap()
        var name = String()

        if (brick is UserVariableBrickInterface) {
            map = BackpackListManager.getInstance().backpack.backpackedUserVariables[groupName]
            name = brick.userVariable.name
        } else if (brick is UserListBrick) {
            map = BackpackListManager.getInstance().backpack.backpackedUserLists[groupName]
            name = brick.userList.name
        }

        if (!isUserDataAlreadyInScript(map, name, type)) {
            map?.set(name, type)
        }
    }

    private fun isUserDataAlreadyInScript(map: HashMap<String, Int>?, userDataName: String, variableType: Int): Boolean = map?.get(userDataName) == variableType

    @Throws(IOException::class, CloneNotSupportedException::class)
    fun packForSprite(scriptToPack: Script, destinationSprite: Sprite) {
        val script = scriptToPack.clone()
        val scriptFlatBrickList: List<Brick> = ArrayList()
        script.addToFlatList(scriptFlatBrickList)

        for (brick in scriptFlatBrickList) {
            when (brick) {
                is SetLookBrick ->
                    brick.look = brick.look?.let { look ->
                        lookController.packForSprite(look, destinationSprite)
                    }

                is WhenBackgroundChangesBrick ->
                    brick.look = brick.look?.let { look ->
                        lookController.packForSprite(look, destinationSprite)
                    }

                is PlaySoundBrick ->
                    brick.sound = brick.sound?.let { sound ->
                        soundController.packForSprite(sound, destinationSprite)
                    }

                is PlaySoundAndWaitBrick ->
                    brick.sound = brick.sound?.let { sound ->
                        soundController.packForSprite(sound, destinationSprite)
                    }
            }
        }
        destinationSprite.scriptList.add(script)
    }

    @Throws(CloneNotSupportedException::class)
    fun unpack(scriptName: String, scriptToUnpack: Script, destinationSprite: Sprite) {
        val script = scriptToUnpack.clone()
        copyBroadcastMessages(script.scriptBrick)
        for (brick in script.brickList) {
            if (projectManager.currentProject.isCastProject &&
                CastManager.unsupportedBricks.contains(brick.javaClass)
            ) {
                Log.e(TAG, "CANNOT insert bricks into ChromeCast project")
                return
            }
            unpackUserVariable(brick, scriptName)
            unpackUserList(brick, scriptName)
            copyBroadcastMessages(brick)
        }
        destinationSprite.scriptList.add(script)
    }

    private fun unpackUserVariable(brick: Brick, scriptName: String) {
        if (brick is UserVariableBrickInterface) {
            val variableToCheck = brick.userVariable
            val variableType: Int? =
                BackpackListManager.getInstance().backpack.backpackedUserVariables[scriptName]?.get(variableToCheck.name)

            var listToAddTo: MutableList<UserVariable> = projectManager.currentProject.userVariables

            when (variableType) {
                LOCAL_USER_VARIABLE -> listToAddTo = projectManager.currentSprite.userVariables
                MULTIPLAYER_USER_VARIABLE -> listToAddTo = projectManager.currentProject.multiplayerVariables
            }

            addNewUserVariable(variableToCheck, listToAddTo, brick)
        }
    }

    private fun unpackUserList(brick: Brick, scriptName: String) {
        if (brick is UserListBrick) {
            val listToCheck = brick.userList
            val listType: Int? =
                BackpackListManager.getInstance().backpack.backpackedUserLists[scriptName]?.get(listToCheck.name)

            if (listType == GLOBAL_USER_VARIABLE) {
                addNewUserList(listToCheck, projectManager.currentProject.userLists, brick)
            } else if (listType == LOCAL_USER_VARIABLE) {
                addNewUserList(listToCheck, projectManager.currentSprite.userLists, brick)
            }
        }
    }

    private fun addNewUserVariable(oldVariable: UserVariable, listToAddTo: MutableList<UserVariable>, brick: UserVariableBrickInterface) {
        if (!listToAddTo.any { variable -> variable.name == oldVariable.name }) {
            val newNameForVariable = UniqueNameProvider().getUniqueName(oldVariable.name, createListOfAllUserVariables())

            val newUserVariable = UserVariable(oldVariable)
            newUserVariable.name = newNameForVariable
            listToAddTo.add(newUserVariable)
            brick.userVariable = newUserVariable
        }
    }

    private fun addNewUserList(oldList: UserList, listToAddTo: MutableList<UserList>, brick: UserListBrick) {
        if (!listToAddTo.any { list -> list.name == oldList.name }) {
            val newNameForList = UniqueNameProvider().getUniqueName(oldList.name, createListOfAllUserList())

            val newUserList = UserList(oldList)
            newUserList.name = newNameForList
            listToAddTo.add(newUserList)
            brick.userList = newUserList
        }
    }

    private fun createListOfAllUserVariables(): List<String> {
        val listOfAllVariables: ArrayList<String> = ArrayList()

        for (variable in projectManager.currentProject.userVariables) {
            listOfAllVariables.add(variable.name)
        }

        for (variable in projectManager.currentSprite.userVariables) {
            listOfAllVariables.add(variable.name)
        }

        for (variable in projectManager.currentProject.multiplayerVariables) {
            listOfAllVariables.add(variable.name)
        }

        return listOfAllVariables
    }

    private fun createListOfAllUserList(): List<String> {
        val listOfAllLists: ArrayList<String> = ArrayList()

        for (list in projectManager.currentProject.userLists) {
            listOfAllLists.add(list.name)
        }

        for (list in projectManager.currentSprite.userLists) {
            listOfAllLists.add(list.name)
        }

        return listOfAllLists
    }

    private fun copyBroadcastMessages(brick: Brick): Boolean {
        if (brick is BroadcastMessageBrick) {
            val broadcastMessage = brick.broadcastMessage
            return projectManager.currentProject.broadcastMessageContainer.addBroadcastMessage(
                broadcastMessage
            )
        }
        return false
    }

    @Throws(IOException::class, CloneNotSupportedException::class)
    fun unpackForSprite(
        scriptToUnpack: Script,
        destinationProject: Project?,
        destinationScene: Scene?,
        destinationSprite: Sprite
    ) {
        val script = scriptToUnpack.clone()
        for (brick in script.brickList) {
            when {
                projectManager.currentProject.isCastProject && CastManager.unsupportedBricks.contains(
                    brick.javaClass
                ) -> {
                    Log.e(TAG, "CANNOT insert bricks into ChromeCast project")
                    return
                }
                brick is SetLookBrick && brick.look != null ->
                    brick.look = lookController.unpackForSprite(
                        brick.look,
                        destinationScene,
                        destinationSprite
                    )

                brick is WhenBackgroundChangesBrick && brick.look != null ->
                    brick.look = lookController.unpackForSprite(
                        brick.look,
                        destinationScene,
                        destinationSprite
                    )

                brick is PlaySoundBrick && brick.sound != null ->
                    brick.sound = soundController.unpackForSprite(
                        brick.sound,
                        destinationScene,
                        destinationSprite
                    )

                brick is PlaySoundAndWaitBrick && brick.sound != null ->
                    brick.sound = soundController.unpackForSprite(
                        brick.sound,
                        destinationScene,
                        destinationSprite
                    )

                brick is UserVariableBrickInterface && brick.userVariable != null ->
                    updateUserVariable(brick, destinationProject, destinationSprite)

                brick is UserListBrick && brick.userList != null ->
                    updateUserList(brick, destinationProject, destinationSprite)

                brick is UserDataBrick ->
                    updateUserData(brick, destinationProject, destinationSprite)
            }
        }
        destinationSprite.scriptList.add(script)
    }

    private fun updateUserData(
        brick: UserDataBrick,
        destinationProject: Project?,
        destinationSprite: Sprite?
    ) {
        for (entry in brick.userDataMap.entries) {
            val previousUserData = entry.value
            var updatedUserList: UserData<*>?
            val scope = destinationSprite?.let { sprite -> Scope(destinationProject, sprite, null) }
            updatedUserList = if (BrickData.isUserList(entry.key)) {
                UserDataWrapper.getUserList(previousUserData?.name, scope)
            } else {
                UserDataWrapper.getUserVariable(previousUserData?.name, scope)
            }
            entry.setValue(updatedUserList)
        }
    }

    private fun updateUserList(
        brick: UserListBrick,
        destinationProject: Project?,
        destinationSprite: Sprite?
    ) {
        val previousUserList = brick.userList
        val scope = destinationSprite?.let { sprite -> Scope(destinationProject, sprite, null) }
        val updatedUserList = UserDataWrapper.getUserList(previousUserList?.name, scope)
        brick.userList = updatedUserList
    }

    private fun updateUserVariable(
        brick: UserVariableBrickInterface,
        destinationProject: Project?,
        destinationSprite: Sprite?
    ) {
        val previousUserVar = brick.userVariable
        val scope = destinationSprite?.let { sprite -> Scope(destinationProject, sprite, null) }
        val updatedUserVar =
            UserDataWrapper.getUserVariable(previousUserVar?.name, scope)
        brick.userVariable = updatedUserVar
    }
}
