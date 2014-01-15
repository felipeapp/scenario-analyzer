<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Transferência de Solicitação de Levantamento Bibliográfico.</h2>
	
	<h:form id="form" enctype="multipart/form-data">
		
		<a4j:keepAlive beanName="levantamentoBibliograficoInfraMBean" />
		
		<table class="formulario" width="80%">
			<caption>Transferência de Solicitação</caption>
			<tbody>
				<tr>
					<th>Biblioteca:</th>
					<td>
						<h:selectOneMenu id="biblioteca" value="#{levantamentoBibliograficoInfraMBean.biblioteca.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{levantamentoBibliograficoInfraMBean.bibliotecasDeTransferenciaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Transferir Solicitação"
								action="#{levantamentoBibliograficoInfraMBean.transferirSolicitacao}" />
						<h:commandButton value="<< Voltar"
								action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantFuncBiblioteca}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>