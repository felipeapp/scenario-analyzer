<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Projetos que Participo </h2>
		
	<h:form id="form">
	
		<input type="hidden" name="lista_origem" id="lista_origem" value="${listaOrigem}"/>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto
		</div>
		
			<table class="listagem">
					<caption class="listagem">Lista dos projetos que o usuário participa</caption>
					<thead>
						<tr>
							<th style="width: 40%">Projeto</th>
							<th>Categoria</th>
							<th>Função</th>
							<th style="text-align: right;">Ch</th>
							<th style="text-align: center;">Início</th>
							<th style="text-align: center;">Fim</th>
							<th></th>							
						</tr>
					</thead>
					<tbody>
							
						<c:set var="projeto" value=""/>
						<c:forEach items="#{membroProjeto.membrosProjetos}" var="item" varStatus="status">						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td>${item.projeto.titulo}</td>
								<td>${item.categoriaMembro.descricao}</td>
								<c:set var="cor" value="${ item.coordenadorProjeto ? 'red' : 'black' }" />
								<td><font color="${cor}">${item.funcaoMembro.descricao}</font></td>
								<td style="text-align: right;">${item.chDedicada} h</td>
								<td style="text-align: center;"><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></td>									
								<td style="text-align: center;"><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></td>
								
								<td width="2%">
									<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=viewProjeto&idProjeto=${item.projeto.id}">
										<img src="${ctx}/img/view.gif"
											title="Visualizar Projeto"
											alt="Visualizar Projeto"/>
									</html:link>
								</td>									
							</tr>
				 		</c:forEach>
				 		   
			 		    <c:if test="${empty membroProjeto.membrosProjetos}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">Não há membros de equipe cadastrados em ações ativas coordenadas pelo usuário atual.</font></td></tr>
			 		    </c:if>
				 		   
				   </tbody>	
			</table>
			
		</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>