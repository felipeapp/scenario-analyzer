<%@include file="../include/cabecalho.jsp"%>

 <f:view>
	<div data-role="page" id="page-aulas-docente" data-theme="b" data-add-back-btn="true">
	
		<div data-role="header" data-theme="b">
			<h1>Lista de Aulas</h1>
		</div>
		
		<div data-role="content">		
			<%@include file="/mobile/touch/include/mensagens.jsp"%>		
     		<h:form id="form-aulas-docente">     		
				    <ul data-role="listview" data-inset="true" data-filter="true"  data-filter-placeholder="Buscar aula..." data-filter-theme="b" data-theme="b" data-dividertheme="b">
	       				<li data-role="list-divider"><h:outputText value="#{ portalDocenteTouch.turma.disciplina.nome }  - #{portalDocenteTouch.turma.descricaoHorario}"/></li>
				    	<a4j:repeat var="aula" value="#{portalDocenteTouch.aulas}">
			           		<li>
		  	           			<h:commandLink style="white-space: normal;" id="linkIrParaAula" value="#{ aula.descricao } (#{ aula.dataFormatada } - #{ aula.dataFimFormatada })"
	    				  				action="#{ portalDocenteTouch.exibirTopico }">
									<f:setPropertyActionListener value="#{ aula.id }" target="#{ portalDocenteTouch.topicoSelecionado.id }"/>
								</h:commandLink>
							</li>
						</a4j:repeat>
				    </ul>		
  		  	</h:form>
		</div><!-- data-role="content" -->
		
	</div> <!-- data-role="page" -->
</f:view>
<%@include file="../include/rodape.jsp"%>
