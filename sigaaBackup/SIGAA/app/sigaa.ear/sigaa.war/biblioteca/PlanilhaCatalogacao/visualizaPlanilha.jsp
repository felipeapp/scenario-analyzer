<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<style>
		.etiqueta {
			width: 20%;
		}
		
		.indicador {
			width: 10%;
		}
		
		.acao {
			width: 2px;
		}
	</style>


	<h2><ufrn:subSistema /> > Visualizar Planilha </h2>
	<br>
	
	<h:form id="formularioPlanilha">
	
	
		<a4j:keepAlive beanName="planilhaMBean" />
	
		<div class="descricaoOperacao" style="width:85%">
			<p>Dados da planilha selecionada</p>
		</div>

	
	
	
		<table class="visualizacao" width="85%">
			<caption> Planilha  ${planilhaMBean.obj.nome} </caption>

			<tr>
				<th style="width: 20%">Descrição:</th>
				<td>
					<h:outputText value="#{planilhaMBean.obj.descricao}"/>
				</td>
			</tr>
			
			<c:if test="${planilhaMBean.obj.tipo == planilhaMBean.bibliografica}">
				<tr>
					<th style="width: 20%"> Formato do Material:</th>
					<td>
						<h:outputText id="formato" value="#{planilhaMBean.obj.formatoMaterial.descricaoCompleta}" />
					</td>
				</tr>
			</c:if>
	
			<%-- Dados dos campos de controle  --%>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
		
						<caption class="listagem">Campos de Controle</caption>
	
						
						<c:if test="${planilhaMBean.obj.qtdObjetosControle > 0 }">
						
							<c:forEach var="objetoControle" items="#{planilhaMBean.obj.objetosControlePlanilhaOrdenadosByTag}">
								<tr>
									<td width="20%" style="text-align: right;">
									${objetoControle.tagEtiqueta}:	
									</td>
									<td colspan="3">
									${objetoControle.dadoParaExibicao}	
									</td>
								</tr>						
		
		
							</c:forEach>
						</c:if>
								
					</table>
				</td>
			</tr>
			
			<%-- Dados dos campos de dados  --%>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
	
						<caption class="listagem">Campos de Dados</caption>
						
						<c:if test="${planilhaMBean.obj.qtdObjetosDados > 0}">
							<c:forEach var="objetoDados" items="#{planilhaMBean.obj.objetosDadosPlanilhaOrdenadosByTag}">
								<tr>
									<td width="20%" style="text-align: right;">
										${objetoDados.tagEtiqueta}:
									</td>
									<td style="width: 5%;">
		
										<c:if test="${objetoDados.indicador1 == ' '}">
											_ 
										</c:if> 
										<c:if test="${objetoDados.indicador1 != ' '}">
											${objetoDados.indicador1}	
										</c:if>
		
									</td>
									<td style="width: 5%;">
		
										<c:if test="${objetoDados.indicador2 == ' '}">
											_ 
										</c:if> 
										<c:if test="${objetoDados.indicador2 != ' '}">
											${objetoDados.indicador2}	
										</c:if>
		
									</td>
									<td style="width: 80%;">		
										${objetoDados.subCampos}
									</td>
		
								</tr>						
		
		
							</c:forEach>
						</c:if>
						
					</table>
				</td>
			</tr>
			
			
				
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Cancelar" action="#{planilhaMBean.voltar}" immediate="true" id="cancelar" />
					</td>
				</tr>
			</tfoot>
			
		</table>
			
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>