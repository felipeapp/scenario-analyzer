<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>
	<h2><ufrn:subSistema /> > Tipos de Empr�stimo </h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo est�o listados os Tipos de Empr�stimos que podem ser realizados no sistema. </p>
    	<br/>
    	<p> <strong>Um Tipo de Empr�stimo indica situa��es especiais em que um empr�stimo pode ocorrer, permindo que um mesmo material possua prazos e quantidades diferentes 
    		dependendo do Tipo de Empr�stimo escolhido.</strong>
    	</p>
    	<p> N�o podem existir dois os mais Tipos de Empr�stimos com a mesma descri��o no sistema.  Existem alguns Tipos de Empr�stimos 
    	que s�o fixos no sistema e n�o podem ser alterados, os demais podem ser criados e removidos livremente.</p>
		<br/>
		<p><strong>IMPORTANTE:</strong> Ap�s cadatrar um novo Tipo de Empr�stimo, deve-se configurar nas repectivas Pol�ticas de Empr�stimos os prazos e quantidades que os materiais emprestados por esse Tipo de Empr�stimos possuir�o.</p>
	</div>
	
	<a4j:keepAlive beanName="tipoEmprestimo" />
	
	<h:form>

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
			<div class="infoAltRem" style="width:80%;">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{tipoEmprestimo.preCadastrar}" value="Novo Tipo de Empr�stimo" />
	
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Tipo de Empr�stimo
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Tipo de Empr�stimo
			</div>
		</ufrn:checkRole> 



		<table class="listagem" style="width:80%;">
			<caption>Lista de Tipos de Empr�stimos (${tipoEmprestimo.size})</caption>
			<thead>
				<tr>
					<th>Descri��o</th>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<th width="20"></th>
						<th width="20"></th>
					</ufrn:checkRole>
					
				</tr>
			</thead>
			<c:forEach items="#{tipoEmprestimo.all}" var="t" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${t.descricao}</td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
					<td>
						<h:commandLink title="Alterar Tipo de Empr�stimo" action="#{tipoEmprestimo.preAtualizar}" rendered="#{t.alteravel}">
							<f:param name="id" value="#{t.id}" />
							<h:graphicImage url="/img/alterar.gif" alt="Alterar Tipo" />
						</h:commandLink>
					</td>
					<td>
						<h:commandLink title="Remover Tipo de Empr�stimo"  rendered="#{t.alteravel}"
							action="#{tipoEmprestimo.remover}" onclick="return confirm('Tem certeza que deseja remover este Tipo de Empr�stimo ?');">
							<f:param name="idTipoEmprestimo" value="#{t.id}" />
							<h:graphicImage url="/img/delete.gif" alt="Remover Tipo." />
						</h:commandLink>
					</td>
					</ufrn:checkRole>
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{tipoEmprestimo.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>