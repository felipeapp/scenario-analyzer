<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<html>
	<head>
		<title>SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas</title>
		
		<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
		<script type="text/javascript">
			JAWR.loader.style('/bundles/css/sigaa_base.css');
		</script>                
	</head>
	
	<body>
		<f:view>
		<h:form prependId="false">
		
		<center>
			<br/> <br/>
			<h3>Por favor, aguarde enquanto carregamos as suas permissões...</h3>
			<br/> <br/> 
			
			<a4j:outputPanel>
				<rich:progressBar value="#{ logonProgress.currentPercent }" 
				   		label="#{ logonProgress.currentPercent }%" id="progresso"
				   		minValue="0" maxValue="100" interval="500" />
			</a4j:outputPanel>
		</center>
			        		
		<script type="text/javascript">
			Event.observe(window, 'load', function() {
				document.location.href = '/sigaa/paginaInicial.do';
			}, false); 
		</script>
		
		</h:form>
		</f:view>
	</body>
</html>