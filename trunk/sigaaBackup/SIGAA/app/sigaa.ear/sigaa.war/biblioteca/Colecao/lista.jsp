<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>
	<h2><ufrn:subSistema /> &gt; Coleções</h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo estão listadas as coleções que os materiais informacionais do acervo podem possuir. </p>
    	<br/>
    	<p> <strong> Uma coleção indica um agrupamento ao qual os materiais do acervo pertencem, pode indicar uma localização física especial nas bibliotecas. </strong></p> 
    	<p> Não podem existir duas os mais coleções com a mesma descrição no sistema. </p>
		<p> Se uma coleção é utilizada no Registro de Consutal significa que essa coleção aceita que seus materiais sejam registrados nas consultas presenciais dos usuários nas bibliotecas.</p>
	</div>
	
	<h:form>

		<div class="infoAltRem" style="width:90%;">
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{colecaoMBean.preCadastrar}" value="Nova Coleção" />
			
			
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Coleção
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Coleção
			
			</ufrn:checkRole>
		</div> 

		<table class="formulario" width="90%">
			<caption class="listagem">Lista de Coleções (${colecaoMBean.size})</caption>
			<thead>
				<tr>
					<th style="width: 60%;">Descrição</th>
					
					<th style="width: 30%;">Utilizada no Registro de Consulta</th>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<th style="width: 5%;"></th>
						<th style="width: 5%;"></th>
					</ufrn:checkRole>
				</tr>
			</thead>
			
			<c:forEach items="#{colecaoMBean.all}" var="c" varStatus="status">
				
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
					<td>${c.descricaoCompleta}</td>
					
					<td style="text-align: center; ${c.contagemMovimentacao ? 'color:green; font-weight:bold;' : ''}"> <ufrn:format type="simNao" valor="${c.contagemMovimentacao}" /></td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<td>
							<h:commandLink action="#{colecaoMBean.preAtualizar}" title="Alterar Coleção">
								<f:param name="id" value="#{c.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Coleção" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Remover Coleção" action="#{colecaoMBean.preRemover}">
								<f:param name="id" value="#{c.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Coleção" />
							</h:commandLink>
						</td>
					</ufrn:checkRole>
					
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="4" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{colecaoMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>