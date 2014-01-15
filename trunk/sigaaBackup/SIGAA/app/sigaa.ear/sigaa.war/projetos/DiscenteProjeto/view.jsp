<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>
	<h2>Visualização de Discente de Projeto</h2>
	
<table class="tabelaRelatorio" width="100%">
<caption> Dados de discente de projeto </caption>
<tbody>
	<tr>
		<th width="20%"><b>Ano:</b></th>
		<td><h:outputText value="#{discenteProjetoBean.obj.projeto.ano}"/></td>
	</tr>	

	<tr>
		<th><b>Título:</b></th>
		<td><h:outputText value="#{discenteProjetoBean.obj.projeto.titulo}"/></td>
	</tr>	

	<tr>
		<th><b>Discente:</b></th>
		<td><h:outputText value="#{discenteProjetoBean.obj.discente.matriculaNome}"/></td>
	</tr>

	<tr>
		<th><b>Curso:</b></th>
		<td><h:outputText value="#{discenteProjetoBean.obj.discente.curso.nomeCompleto}" rendered="#{not empty discenteProjetoBean.obj.discente.curso}"/></td>
	</tr>
	
	<tr>
		<th><b>Vínculo:</b></th>
		<td><h:outputText value="#{discenteProjetoBean.obj.tipoVinculo.descricao}"/></td>
	</tr>	

	<tr>
		<th><b>Situação:</b></th>
		<td><h:outputText value="#{discenteProjetoBean.obj.situacaoDiscenteProjeto.descricao}"/></td>
	</tr>	
	
	<tr>
		<th><b>Data de Início:</b></th>
		<td><h:outputText value="#{discenteProjetoBean.obj.dataInicio}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></td>
	</tr>						


	<tr>
		<th><b>Data de Fim:</b></th>
		<td><h:outputText value="#{discenteProjetoBean.obj.dataFim}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></td>
	</tr>
	

	<%-- somente gestores de ações associadas tem acesso aos dados bancários do discente --%>
	<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.GESTOR_BOLSAS_ACOES_ASSOCIADAS}%>">
		<tr>
			<td colspan="2">
				<table class="subFormulario" width="100%">		
					<caption class="listagem"> Dados Bancários </caption>
						<tr>
							<th width="20%"><b>Banco:</b></th>
							<td><h:outputText value="#{discenteProjetoBean.obj.banco.codigoNome}"/></td>
						</tr>
						<tr>
							<th><b>Agência:</b></th>
							<td><h:outputText value="#{discenteProjetoBean.obj.agencia}"/></td>
						</tr>
						<tr>
							<th><b>Conta Corrente:</b></th>
							<td><h:outputText value="#{discenteProjetoBean.obj.conta}"/></td>
						</tr>
						<c:if test="${discenteProjetoBean.obj.operacao != null}">
						<tr>
							<th><b>Operação:</b></th>
							<td><h:outputText value="#{discenteProjetoBean.obj.operacao}"/></td>
						</tr>						
						</c:if>
				</table>
			</td>
		</tr>
	</ufrn:checkRole>
	
	</tbody>
</table>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>