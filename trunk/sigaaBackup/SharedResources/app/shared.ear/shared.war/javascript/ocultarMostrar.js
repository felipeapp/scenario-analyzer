function mostrarElemento(id) 
{
    var targetElement = document.getElementById(id);		
	targetElement.style.display = "";
	
}

function ocultarElemento(id) 
{
    var targetElement = document.getElementById(id);		 
	targetElement.style.display = "none";		    
}

function desabilitaElemento(id) 
{
    var targetElement = document.getElementById(id);		
	targetElement.disabled=true;
	
}

function habilitaElemento(id) 
{
    var targetElement = document.getElementById(id);	    	
	targetElement.disabled=false;
	
}
