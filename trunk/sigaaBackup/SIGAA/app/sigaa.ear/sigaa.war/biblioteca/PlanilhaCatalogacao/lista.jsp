<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>
	<h2><ufrn:subSistema /> > Planilhas ${planilha.nomePlanilha}</h2>
	
	
	<div class="descricaoOperacao"> 
	    <p>Planilhas de Catalogação cadatras no Sistema.</p>
	    <p>Planilhas de Catalogação são planilhas que contém as informações dos campos e sub campos MARC mais comumente usados para inserir as informações no acervo.</p>
		<p>As Planilhas de catalogação são usadas para esconder os detalhes de toda a codificação MARC 
			<span style="font-style: italic;"> (Campos de Controle, Indicadores, Tag dos Campos de Dados e Códigos dos Sub Campos). </span> </p>
			
		<p>Uma vez que todas essas informações de códificação MARC estejam no sistema, o catalogador não precisa mais ser preocupar com elas.</p>	
		<br/><br/>	
		<p>Para o cadastro de uma planilha deve-se ter um bom conhecimento do padrão MARC.</p>	
	</div>
	
	<h:form>

		<a4j:keepAlive beanName="planilhaMBean"></a4j:keepAlive>

		<div class="infoAltRem">
			
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{planilhaMBean.preCadastrar}" value="Nova Planilha"><f:param name="tipoPlanilha" value="#{planilhaMBean.obj.tipo}" /></h:commandLink>
			</ufrn:checkRole>
			
			<h:graphicImage value="/img/view.gif" />: Visualizar Planilha
			
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">	
				<h:graphicImage value="/img/alterar.gif" />: Alterar Planilha
				<h:graphicImage value="/img/delete.gif" />: Remover Planilha
			</ufrn:checkRole>
			
		</div>

		<%-- As listagem de planilhas cadastradas no sistema --%>
		<table class="listagem" width="100%">
			
			<caption>Planilhas ${planilhaMBean.descricaoTipoPlanilha}</caption>
			
			<c:if test="${ empty planilhaMBean.all }">
				<tr>
					<td><p style="text-align:center;color:red;">Não Existem planilhas Cadastradas</p></td>
				</tr>
			</c:if>

			<c:if test="${ not empty planilhaMBean.all }">
				
				<thead>
					<tr>
						<th style="width: 30%;">Nome</th>
						<th style="width: 65%;">Descrição</th>
						<th colspan="3">Ações</th>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach var="planilha" items="#{planilhaMBean.all}" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${planilha.nome}</td>
							<td>${planilha.descricao}</td>
							<td width="20">
								<h:commandLink title="Visualizar Planilha" action="#{planilhaMBean.visualizar}">
									<f:param name="id" value="#{planilha.id}"></f:param>
									<h:graphicImage url="/img/view.gif" alt="Visualizar Planilha" />
								</h:commandLink>
							</td>
							<td width="20">
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
								<h:commandLink title="Alterar Planilha" action="#{planilhaMBean.preAtualizar}">
									<f:param name="id" value="#{planilha.id}"></f:param>
									<h:graphicImage url="/img/alterar.gif" alt="Alterar Planilha" />
								</h:commandLink>
								</ufrn:checkRole>
							</td>
							<td width="20">
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
								<h:commandLink title="Remover Planilha" action="#{planilhaMBean.remover}" 
									onclick="if (!confirm('Tem certeza que deseja remover esta planilha?')) return false;">
									<h:graphicImage url="/img/delete.gif" alt="Remover Planilha" />
									<f:param name="id" value="#{planilha.id}"></f:param>
								</h:commandLink>
								</ufrn:checkRole>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="5" style="text-align: center;">
						<h:commandButton value="<< Voltar" action="#{planilhaMBean.cancelar}"></h:commandButton>
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
