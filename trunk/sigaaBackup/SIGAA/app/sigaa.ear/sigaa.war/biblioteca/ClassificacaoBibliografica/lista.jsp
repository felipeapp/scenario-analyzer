<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>
	<a4j:keepAlive beanName="classificacaoBibliograficaMBean" />
	
	<h2><ufrn:subSistema /> > Classifica��es Bibliogr�ficas</h2>
	<br>
	
	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<p>Nesta p�gina � poss�vel configurar as classifica��es bibliogr�ficas utilizadas no sistema. </p>
		<p>O sistema surporta a utiliza��o de at� tr�s classifica��es bibliogr�ficas simultaneamente. </p>
		<p>A configura��o das classifica��es bibliogr�ficas deve ser a primera opera��o a ser realizada no sistema, sem essa 
		configura��o n�o ser� poss�vel incluir dados no acervo. Depois de cadastradas, as informa��es das classifica��es n�o podem 
		ser alteradas, porque isso implicaria em inconsist�ncias na base de dados do acervo.</p>
	</div>
	
	<h:form>
		<h:messages showDetail="true" />

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
			<div class="infoAltRem" style="width:80%;">
	
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{classificacaoBibliograficaMBean.preCadastrar}" value="Nova Classifica��o Bibliogr�fica" />
	
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Classifica��o
				
			</div>
		</ufrn:checkRole>

		<%-- Para n�o ficar buscando v�rias vezes no banco --%>
		<c:set var="listaClassificacaoBibliografica" value="${classificacaoBibliograficaMBean.classificacoesAtivas}" scope="request" />

		<table class="listagem"" width="80%">
			<caption>Lista de Classifica��es Bibliogr�ficas(${fn:length(listaClassificacaoBibliografica)})</caption>
			<thead>
				<tr>
					<th>Descri��o</th>
					<th>Ordem</th>
					<th>Campo MARC</th>
					<th>Classes Principais</th>
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<%-- <th width="20"></th> --%>
						<th width="20"></th>
					</ufrn:checkRole>
				</tr>
			</thead>
			
			<c:forEach items="#{listaClassificacaoBibliografica}" var="c" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${c.descricao}</td>
					<td>${c.ordem.descricao}</td>
					<td>${c.campoMARC.descricao}</td>
					<td>${c.descricaoClassesPrincipais}</td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<%-- <td>
							<h:commandLink title="Alterar" action="#{classificacaoBibliograficaMBean.preAtualizar}"
								style="border: 0;">
								<f:param name="id" value="#{c.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Classifica��o" />
							</h:commandLink>
						</td> --%>
						<td>
							<h:commandLink title="Remover Classifica��o" style="border: 0;"
								action="#{classificacaoBibliograficaMBean.remover}"
								onclick="return confirm('Confirma remo��o da classifica��o bibliogr�fica?');">
								<f:param name="id" value="#{c.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Classifica��o Bibliogr�fica" />
							</h:commandLink>
						</td>
					</ufrn:checkRole>
					
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="5" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{classificacaoBibliograficaMBean.cancelar}" immediate="true" id="cancelar" onclick="#{confirm}"></h:commandButton>
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>