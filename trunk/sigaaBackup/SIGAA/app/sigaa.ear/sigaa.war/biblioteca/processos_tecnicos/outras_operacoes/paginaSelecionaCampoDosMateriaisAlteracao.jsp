<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<h2>  <ufrn:subSistema /> &gt; Alterar as Informações de Vários Materiais &gt; Selecionar Campo para Alteração</h2>

<f:view>

	<h:form>
	
		<a4j:keepAlive beanName="alteraDadosVariosMateriaisMBean"></a4j:keepAlive>

		<%-- Caso o usuário deseja voltar para a tela de pesquisa --%>
		<%-- <a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive> --%>

		<%-- Caso o usuário deseja voltar para a tela de pesquisa --%>
		<a4j:keepAlive beanName="pesquisaMateriaisInformacionaisMBean"></a4j:keepAlive>

		
		<table class="formulario" width="100%">
			<caption> Selecione o campo para alteração </caption>


			<tr>
				<th style=" width: 20%">Campos para Alteração:</th>
				<td>
					<table style="width: 100%;">
					
						<c:set var="quebraLinha" value="0" scope="request" />
						
						<c:if test="${fn:length(alteraDadosVariosMateriaisMBean.camposDoMaterial) > 0}">
							<c:forEach items="#{alteraDadosVariosMateriaisMBean.camposDoMaterial}" var="campo" varStatus="status">
							
								<c:if test="${(status.index % 4 == 0 && quebraLinha == 0) || status.index == 1}">
									<tr>
								</c:if>
							
								<td style="width: 400px;" >
									<input type="radio" id="radioCampoDoMaterialParaAlteracao" name="campoAlteracao" value="${campo}"  ${status.index == 0 ? "checked=\"checked\" ": " "}> ${campo}
								</td>
							
								<c:if test="${status.index % 4 == 0  && quebraLinha == 1}">
									</tr>
									<c:set var="quebraLinha" value="0" scope="request" />
								</c:if>
								<c:if test="${status.index % 4 == 0  && quebraLinha == 0 || status.index == 1}">
									<c:set var="quebraLinha" value="1" scope="request" />
								</c:if>
							
							
							</c:forEach>
						</c:if>
						
					</table>
				</td>
			</tr>

		<tfoot>
				<tr>				
					<td colspan="9" style="text-align: center;">
						<c:if test="${fn:length(alteraDadosVariosMateriaisMBean.camposDoMaterial) > 0}">
							<h:commandButton id="cmdButtonVoltar"  value="<< Voltar"  action="#{alteraDadosVariosMateriaisMBean.voltarTelaSelecionaMateriais}"/>
						</c:if>
						
						<h:commandButton id="cmdButtonCancelar" value="Cancelar"  action="#{alteraDadosVariosMateriaisMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
						
						<c:if test="${fn:length(alteraDadosVariosMateriaisMBean.camposDoMaterial) > 0}">	
							<h:commandButton id="cmdButtonProximo" value="Próximo >>"  action="#{alteraDadosVariosMateriaisMBean.verificaCampoAlteracaoEscolhido}"/>
						</c:if>
					</td>
				</tr>
			</tfoot>
			
		</table>


	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>