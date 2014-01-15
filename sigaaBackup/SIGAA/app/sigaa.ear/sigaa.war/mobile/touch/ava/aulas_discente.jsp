<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-aulas-discente" data-theme="b" data-add-back-btn="true">
	
		<div data-role="header" data-theme="b">
			<h1>Lista de Aulas</h1>
		</div>
		
		<div data-role="content">
				<%@include file="/mobile/touch/include/mensagens.jsp"%>				
	     		<h:form id="form-lista-aulas-discente">
	     			<div data-role="fieldcontain">
					    <ul data-role="listview" data-inset="true" data-filter="true" data-filter-placeholder="Buscar aula..." data-filter-theme="b" data-theme="b" data-dividertheme="b">
		       				<li data-role="list-divider"><h:outputText value="#{ portalDiscenteTouch.turma.disciplina.nome }  - #{portalDiscenteTouch.turma.descricaoHorario}"/></li>
					    	<a4j:repeat var="aula" value="#{ portalDiscenteTouch.topicosAulas }">
				           		<li>
			  	           			<h:commandLink style="white-space: normal;" id="linkIrParaAula" value="#{ aula.descricao } (#{ aula.dataFormatada } - #{ aula.dataFimFormatada })"
		    				  				action="#{ portalDiscenteTouch.exibirTopico }">
										<f:setPropertyActionListener value="#{ aula.id }" target="#{ portalDiscenteTouch.topicoSelecionado.id }"/>
									</h:commandLink>
								</li>
							</a4j:repeat>
					    </ul>
					</div>
			  </h:form>
		</div><!-- data-role="content" -->

     </div><!-- data-role="page" -->
</f:view>
<%@include file="../include/rodape.jsp"%>
			
