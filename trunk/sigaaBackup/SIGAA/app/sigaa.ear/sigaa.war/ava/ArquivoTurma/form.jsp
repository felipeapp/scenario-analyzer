<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/yahoo-min.js"></script>
<script type="text/javascript" src="/shared/javascript/yui/event-min.js"></script>
<script type="text/javascript" src="/shared/javascript/yui/dom-min.js"></script>
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

<h2>Incluir Material da Disciplina</h2>
<hr>

<script type="text/javascript">
var criarAbas = function() {
    var tabView = new YAHOO.widget.TabView('tabs-meses');
};

criarAbas();
</script>

<f:view>
	<h:form id="form">
	<h:messages showDetail="true"></h:messages>
		<table class="formulario" width="100%">

			<caption class="listagem">Escolha um arquivo para Associar a turma</caption>
			
			<tr>
				<th valign="top">Associar o arquivo:</th>
				<td><h:outputText value="#{ arquivosTurma.obj.arquivo.nome }"/></td>
			</tr>
			<tr>
				<th>À turma:</th>
				<td>
					<h:selectOneMenu value="#{ arquivosTurma.obj.turma.id }" id="turma">
						<f:selectItems value="#{ portalDocente.turmasAbertasCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				
				 	<div id="tabs-meses" class="yui-navset">
	        			<ul class="yui-nav">
			            	<li><a href="#janeiro" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-janeiro', 'mes=1')"><em>Jan.</em></a></li>
		            		<li><a href="#fevereiro" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-fevereiro', 'mes=2')"><em>Fev.</em></a></li>
			            	<li><a href="#marco" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-marco', 'mes=3')"><em>Mar.</em></a></li>
			            	<li><a href="#abril" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-abril', 'mes=4')"><em>Abr.</em></a></li>
			            	<li><a href="#maio" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-maio', 'mes=5')"><em>Mai.</em></a></li>
			            	<li><a href="#junho" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-junho', 'mes=6')"><em>Jun.</em></a></li>
			            	<li><a href="#julho" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-julho', 'mes=7')"><em>Jul.</em></a></li>
			            	<li><a href="#agosto" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-agosto', 'mes=8')"><em>Ago.</em></a></li>
			            	<li><a href="#setembro" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-setembro', 'mes=9')"><em>Set.</em></a></li>			            				            				            	
			            	<li><a href="#outubro" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-outubro', 'mes=10')"><em>Out.</em></a></li>
			            	<li><a href="#novembro" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-novembro', 'mes=11')"><em>Nov.</em></a></li>
			            	<li><a href="#dezembro" onclick="getContent('/sigaa/portais/turma/ArquivoTurma/conteudo.jsf', 'mes-dezembro', 'mes=12')"><em>Dez.</em></a></li>			            				            				            	
			        	</ul>
			        				        				     
			        	<div class="yui-content" style="min-height: 250px; padding: 1em;">
			        		<div id="mes-janeiro"></div>
				        	<div id="mes-fevereiro"></div>
				        	<div id="mes-marco"></div>
				        	<div id="mes-abril"></div>
				        	<div id="mes-maio"></div>
			    	    	<div id="mes-junho"></div>
			        		<div id="mes-julho"></div>
			        		<div id="mes-agosto"></div>
				        	<div id="mes-setembro"></div>
				        	<div id="mes-outubro"></div>
				        	<div id="mes-novembro"></div>
				        	<div id="mes-dezembro"></div>
				        </div>
				        
					</div>

				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{arquivosTurma.confirmButton}"
						action="#{arquivosTurma.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{arquivosTurma.cancelar}" /></td>
				</tr>
			</tfoot>

		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
