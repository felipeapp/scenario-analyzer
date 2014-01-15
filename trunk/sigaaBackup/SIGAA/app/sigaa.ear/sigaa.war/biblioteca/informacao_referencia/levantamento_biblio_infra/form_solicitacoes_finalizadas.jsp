<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Levantamento Bibliogr�fico</h2>
	
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao" style="width:80%;">
		Esta p�gina exibe os dados levantados a partir de sua solicita��o.
	</div>
	
	<h:form id="form" enctype="multipart/form-data">
	
		<table class="formulario" width="80%">
			<caption>Visualizar Solicita��o de Levantamento Bibliogr�fico</caption>
			
			<tbody>
				<tr>
					<th style="width:180px;">Biblioteca:</th>
					<td>${levantamentoBibliograficoInfraMBean.obj.bibliotecaResponsavel.descricao }</td>
				</tr>
				
				<tr>
					<th>Assunto solicitado:</th>
					<td><h:outputText value="#{levantamentoBibliograficoInfraMBean.obj.detalhesAssunto}" /></td>
				</tr>
				<tr>
					<th>L�nguas aceitas:</th>
					<td>	
						<h:selectManyCheckbox value="#{levantamentoBibliograficoInfraMBean.linguasSelecionadas}" disabled="true">
							<f:selectItems value="#{levantamentoBibliograficoInfraMBean.allLinguas}"/>
						</h:selectManyCheckbox>
						<c:if test="${ not empty levantamentoBibliograficoInfraMBean.obj.outraLinguaDescricao }">
							Outra(s) l�ngua(s): <h:outputText value="#{levantamentoBibliograficoInfraMBean.obj.outraLinguaDescricao}" />
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Per�odo:</th>
					<td>
						<h:selectOneRadio value="#{levantamentoBibliograficoInfraMBean.obj.periodo}" disabled="true">
							<f:selectItems value="#{levantamentoBibliograficoInfraMBean.allPeriodos}"/>
						</h:selectOneRadio>
						<c:if test="${ not empty levantamentoBibliograficoInfraMBean.obj.outroPeriodoDescricao }">
							Outro(s) per�odo(s): <h:outputText value="#{levantamentoBibliograficoInfraMBean.obj.outroPeriodoDescricao}" />
						</c:if>
					</td>
				</tr>
				
				<c:if test="${levantamentoBibliograficoInfraMBean.autorizadoInfra}">
					<tr>
						<th>Solicita��o de Infra-estrutura?</th>
						<td>
							<h:selectOneRadio value="#{levantamentoBibliograficoInfraMBean.obj.infra}" disabled="true">
								<f:selectItem itemValue="true" itemLabel="Sim" />
								<f:selectItem itemValue="false" itemLabel="N�o" />
							</h:selectOneRadio>
						</td>
					</tr>
				</c:if>
				
				<tr>
					<td colspan="2">				
						<table width="100%" class="subFormulario">
							<caption>Atendimento da Solicita��o</caption>
								
							<tr>
								<th style="width:180px;vertical-align:top;">Fontes pesquisadas:</th>
								<td><h:outputText value="#{levantamentoBibliograficoInfraMBean.obj.fontesPesquisadas}" /></td>
							</tr>
								
							<tr>
								<th style="vertical-align:top;">Observa��es:</th>
								<td><h:outputText value="#{levantamentoBibliograficoInfraMBean.obj.observacao}" /></td>
							</tr>
								
							<c:if test="${levantamentoBibliograficoInfraMBean.obj.idArquivo != null}">
								<tr>
									<td colspan="2" style="text-align:center;">Um arquivo foi anexado a esta solicita��o. Para baix�-lo, 
										<h:commandLink action="#{levantamentoBibliograficoInfraMBean.baixarArquivoAnexado}" value="clique aqui">
											<f:param name="id" value="#{levantamentoBibliograficoInfraMBean.obj.id}" />
										</h:commandLink>.
									</td>
								</tr>
							</c:if>
						</table>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<c:if test="${levantamentoBibliograficoInfraMBean.alterar}">
							<h:commandButton value="<< Voltar" action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantFuncBiblioteca}" />
						</c:if>
						<c:if test="${not levantamentoBibliograficoInfraMBean.alterar}">
							<h:commandButton value="<< Voltar" action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantIndividualPorUsuario}" />
						</c:if>
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>