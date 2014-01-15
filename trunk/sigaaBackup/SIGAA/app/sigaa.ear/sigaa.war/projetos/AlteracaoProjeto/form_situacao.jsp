<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.MEMBRO_COMITE_INTEGRADO } %>">

<f:view>
	<a4j:keepAlive beanName="alteracaoProjetoMBean" />
	
	<h2><ufrn:subSistema /> > Alterar Situa��o da A��o Acad�mica Integrada</h2>
	<br>
	<h:form id="form">

		<table class="formulario" width="100%">
			<caption class="listagem">Alterar Situa��o</caption>

			<tr>
				<th width="18%" class="rotulo">N� Institucional:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.numeroInstitucional}" />/<h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th width="18%" class="rotulo">T�tulo:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.titulo}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Ano:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.ano}" /></td>
			</tr>

			<tr>
				<th>Per�odo:</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.dataInicio}" /> at� <h:outputText value="#{alteracaoProjetoMBean.obj.dataFim}" /></td>
			</tr>

			<tr>
				<th class="rotulo">Coordenador(a):</th>
				<td><h:outputText value="#{alteracaoProjetoMBean.obj.coordenador.pessoa.nome}" /></td>
			</tr>

			<tr>
				<th class="rotulo">Situa��o:</th>
				<td><h:selectOneMenu value="#{alteracaoProjetoMBean.obj.situacaoProjeto.id}" id="selectSituacao">
					<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcoesAssociadasValidas}" />
				</h:selectOneMenu></td>
			</tr>
			
			<tr>
				<td><br /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar Altera��o" action="#{alteracaoProjetoMBean.alterarSituacao}" rendered="#{acesso.comissaoIntegrada}" id="btConfirmarAlteracao"/> 
						<h:commandButton value="Cancelar" action="#{alteracaoProjetoMBean.cancelar}" onclick="#{confirm}" id="btCancelarAlteracao"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>