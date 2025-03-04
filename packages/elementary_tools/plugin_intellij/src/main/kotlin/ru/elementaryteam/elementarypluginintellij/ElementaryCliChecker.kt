package ru.elementaryteam.elementarypluginintellij

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import io.flutter.FlutterMessages
import io.flutter.pub.PubRoot
import io.flutter.sdk.FlutterSdk
import kotlinx.coroutines.*

/**
 * Coroutines test
 * TODO(@AlexeyBukin) после тестирования описать нормальную доку
 */
class ElementaryCliChecker {
    companion object {
        /**
         * returns "true" if CLI is NOT installed and dialog with offer to install it was shown
         */
        fun checkIsCliInstalled(
            project: Project,
            sdk: FlutterSdk,
            root: PubRoot?,
        ): Boolean {

            println("checkIsCliInstalled")

//            ATTENTION!!!
//            both variants lead to crush - research
//            runBlocking(Dispatchers.IO + Job()) {
//            runBlocking(Dispatchers.Unconfined) {
//                val a = hasElementaryCliInstalled(project, sdk, root, context)
//                val b = a == a
//            }

            // due to inability to use await for now
            //
            // start of section hasElementaryCliInstalled
            val pubGlobalList = arrayOf(
                "global",
                "list",
            )

            val hasElementaryCliInstalled = runBlocking {
                checkElementaryCli(sdk, root, pubGlobalList)
            }

            if (!hasElementaryCliInstalled) {

                // showInstallDialog start
                val options = arrayOf(
                    "Install",
                    "Not Now",
                )
                val optionInstallIndex = 0
                val dialogResult = FlutterMessages.showDialog(
                    project,
                    "package `elementary_cli` is not installed",
                    "Error: `elementary_cli` is not installed",
                    options,
                    0,
                )
                if (dialogResult == optionInstallIndex) {
                    // install cli
                    val pubFullArguments = arrayOf(
                        "global",
                        "activate",
                        "elementary_cli",
                    )

                    sdk.flutterPub(root, *pubFullArguments).startInConsole(project)
                }
                // showInstallDialog end
            }

            return !hasElementaryCliInstalled
        }

        suspend fun checkElementaryCli(sdk: FlutterSdk, root: PubRoot?, pubGlobalList: Array<String>): Boolean {
            val deferred = CompletableDeferred<Boolean>()

            sdk.flutterPub(root, *pubGlobalList).start(
                { output ->
                    val hasElementaryInList = output.stdoutLines.any { line: String ->
                        line.startsWith("elementary_cli ")
                    }
                    deferred.complete(hasElementaryInList)
                },
                null
            )

            return deferred.await()
        }

//        suspend fun hasElementaryCliInstalled(
//            project: Project,
//            sdk: FlutterSdk,
//            root: PubRoot?,
//            context: DataContext
//        ): Boolean {
//
//            print("hasElementaryCliInstalled")
//
//            val pubFullArguments = arrayOf(
//                "global",
//                "list",
//                "elementary_cli",
//            )
//
//            val deferred = CompletableDeferred<Boolean>()
//
//            val process = sdk.flutterPub(root, *pubFullArguments).start(
//                { output ->
//                    print("lmao")
//                    deferred.complete(false)
//                },
//                null,
////                (output: ProcassOutput) -> true,
////            object : ProcessAdapter() {
////                override fun processTerminated(event: ProcessEvent) {
////                    progressHelper.done()
////                    val exitCode = event.exitCode
////                    if (exitCode != 0) {
////                        FlutterMessages.showError(
////                            "Error installing `elementary_cli`",
////                            "`activate command returned: $exitCode", project
////                        )
////                    } else {
////                        FlutterMessages.showInfo(
////                            "Installed elementary_cli",
////                            "Package `elementary_cli` successfully activated.",
////                            project
////                        )
////                    }
////                }
////            }
//            )
//
//            return deferred.await()
//        }
//
//        fun showInstallCliDialog() {
//
//        }


    }
}
