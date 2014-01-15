/*
* Kudos: Ricardo Wendell
* License: EPL 1.0
* Listener: commandService().addExecutionListener(this); 
* DOM: http://localhost/org.eclipse.eclipsemonkey.lang.javascript
*/

/**
 * Called before any/every command is executed, so we must filter on command ID
 */
function preExecute(commandId, event) {

	// if we see a save command
	//Packages.org.eclipse.jface.dialogs.MessageDialog.openInformation( 	
	//window.getShell(),
	//"Execução de Comando", 
	//commandId);
	
	if (commandId == "teste")
	{
		// execute an organize imports command
   	    var command = commandService().getCommand("org.eclipse.jdt.ui.edit.text.java.organize.imports");
        
        if (command !== null) {
            var newEvent = new Packages.org.eclipse.core.commands.ExecutionEvent(
                command, 
                event.getParameters(), 
                event.getTrigger(), 
                event.getApplicationContext());
                
            command.executeWithChecks(newEvent);
        }
    }
}

/**
 * Returns a reference to the workspace command service
 */
function commandService()
{
	var commandServiceClass = Packages.org.eclipse.ui.commands.ICommandService;
	
	// same as doing ICommandService.class
    var commandService = Packages.org.eclipse.ui.PlatformUI.getWorkbench().getAdapter(commandServiceClass);
    return commandService;
}

/* Add in all methods required by the interface, even if they are unused */
function postExecuteSuccess(commandId, returnValue) {}

function notHandled(commandId, exception) {}

function postExecuteFailure(commandId, exception) {}
