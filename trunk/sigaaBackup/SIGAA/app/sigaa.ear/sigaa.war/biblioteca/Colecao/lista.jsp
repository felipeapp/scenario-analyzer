<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>
	<h2><ufrn:subSistema /> &gt; Cole��es</h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo est�o listadas as cole��es que os materiais informacionais do acervo podem possuir. </p>
    	<br/>
    	<p> <strong> Uma cole��o indica um agrupamento ao qual os materiais do acervo pertencem, pode indicar uma localiza��o f�sica especial nas bibliotecas. </strong></p> 
    	<p> N�o podem existir duas os mais cole��es com a mesma descri��o no sistema. </p>
		<p> Se uma cole��o � utilizada no Registro de Consutal significa que essa cole��o aceita que seus materiais sejam registrados nas consultas presenciais dos usu�rios nas bibliotecas.</p>
	</div>
	
	<h:form>

		<div class="infoAltRem" style="width:90%;">
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{colecaoMBean.preCadastrar}" value="Nova Cole��o" />
			
			
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Cole��o
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Cole��o
			
			</ufrn:checkRole>
		</div> 

		<table class="formulario" width="90%">
			<caption class="listagem">Lista de Cole��es (${colecaoMBean.size})</caption>
			<thead>
				<tr>
					<th style="width: 60%;">Descri��o</th>
					
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
							<h:commandLink action="#{colecaoMBean.preAtualizar}" title="Alterar Cole��o">
								<f:param name="id" value="#{c.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Cole��o" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Remover Cole��o" action="#{colecaoMBean.preRemover}">
								<f:param name="id" value="#{c.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Cole��o" />
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