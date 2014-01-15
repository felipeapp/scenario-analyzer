<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria"%>

<f:view>
	<h2><ufrn:subSistema /> > Validar Resultados da Seleção</h2>
	
	<h:form id="form">

	<center>
   		<table class="formulario" width="90%">
		<caption>Prova Seletiva Selecionada</caption>
		<tbody>
			<tr>
				<th width="25%" class="rotulo">Projeto de Ensino:</th> 
				<td align="left"><h:outputText value="#{validaSelecaoMonitor.prova.projetoEnsino.titulo}"/></td>
			</tr>
			<tr>
				<th width="25%" class="rotulo">Prova: </th> 
				<td align="left">${validaSelecaoMonitor.prova.titulo}</td>
			</tr>
			<tr>
                <th width="25%" class="rotulo">Data da Prova: </th> 
                <td align="left"><fmt:formatDate value="${validaSelecaoMonitor.prova.dataProva}" pattern="dd/MM/yyyy" /></td>
            </tr>			
			<tr>
				<th width="25%" class="rotulo">Vaga(s) Remunerada(s): </th> 
				<td align="left"><h:outputText value="#{validaSelecaoMonitor.prova.vagasRemuneradas}"/></td>
			</tr>
			<tr>
				<th width="25%" class="rotulo">Vaga(s) NÃO Remunerada(s): </th> 
				<td align="left"><h:outputText value="#{validaSelecaoMonitor.prova.vagasNaoRemuneradas}"/></td>
			</tr>
			</tbody>			
		</table>
	</center>

	<br/>
	<br/>	

	<div class="infoAltRem">
	    <h:graphicImage value="/img/edit.png" style="overflow: visible;"/>: Alterar Resultado
	    <h:graphicImage value="/img/monitoria/user1_refresh.png" style="overflow: visible;"/>: Desvalidar Monitoria
	    <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Monitoria
	</div>


	<table class="formulario" width="100%" cellpadding="3">
		<caption class="listagem"> Validação de Resultados da Seleção de Monitores </caption>

		<tbody>
		
			<tr>
				<td>
				
					<h:dataTable value="#{validaSelecaoMonitor.prova.resultadoSelecao}" var="dm" 
						rowClasses="linhaPar,linhaImpar" width="100%" id="listaSelecaoValidada"
						rendered="#{not empty validaSelecaoMonitor.prova.resultadoSelecao}">
						
		
						<rich:column>
							<f:facet name="header"><f:verbatim>Nota</f:verbatim></f:facet>
							<h:outputText value="#{dm.nota}"/>
						</rich:column>
		
						<rich:column style="text-align: right;">
							<f:facet name="header"><f:verbatim>Class.</f:verbatim></f:facet>
							<h:outputText style="text-align: right;" value="#{dm.classificacao}"/>
						</rich:column>
		
						<rich:column>
							<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
							<h:outputText value="#{dm.discente.matriculaNome}"/>
						</rich:column>

						<rich:column>
							<f:facet name="header"><f:verbatim>Situação Validada</f:verbatim></f:facet>
							<h:outputText value="#{dm.situacaoDiscenteMonitoria.descricao}" />					
						</rich:column>

						<rich:column>
							<f:facet name="header"><f:verbatim>Vínculo Validado</f:verbatim></f:facet>
							<h:outputText value="#{dm.tipoVinculo.descricao}"  />
						</rich:column>

						<rich:column>
							<f:facet name="header"><f:verbatim>Observações</f:verbatim></f:facet>
							<h:outputText value="#{dm.observacao}" />
						</rich:column>
						
						<rich:column>
					 		<h:commandLink id="cmdaAlterar" action="#{ validaSelecaoMonitor.iniciarAlterarResultadoMonitor }" rendered="#{dm.passivelValidacao}">
								<h:graphicImage value="/img/edit.png" title="Alterar Resultado"/>
								<f:param name="id" value="#{ dm.id }"/>
							</h:commandLink>
  					 	</rich:column>
						
						<rich:column>
					 		<h:commandLink id="cmdDesvalidar" title="Desvalidar Monitoria" action="#{ validaSelecaoMonitor.desvalidarResultadoMonitor }" 
					 				onclick="return confirm('Tem certeza que deseja Desvalidar este(a) monitor(a)?');" rendered="#{!dm.passivelValidacao && !dm.finalizado }">
								<h:graphicImage value="/img/monitoria/user1_refresh.png" title="Desvalidar Monitoria"/>
								<f:param name="id" value="#{ dm.id }"/>
							</h:commandLink>
  					 	</rich:column>
  					 	
  					 	<rich:column>
							<h:commandLink id="cmdView" action="#{ consultarMonitor.view }">
								<h:graphicImage value="/img/monitoria/user1_view.png" title="Visualizar Monitoria" />
								<f:param name="id" value="#{ dm.id }"/>
							</h:commandLink>
						</rich:column>  					 	  					 			
					</h:dataTable>
				
				</td>
			</tr>
		
			<tr>
				<td align="center">
					<font color="red"><h:outputText value="Lista de participantes do processo seletivo ainda não foi cadastrada." rendered="#{empty validaSelecaoMonitor.prova.resultadoSelecao}"/></font>
				</td>
			</tr>
		</tbody>
	
		<tfoot>
			<tr>
				<td>		
					<h:commandButton value="<< Voltar" action="#{validaSelecaoMonitor.voltaTelaListagem}" id="btVoltar"/>
				</td>
			</tr>
		</tfoot>
	</table>

	<br/>
	
 </h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>