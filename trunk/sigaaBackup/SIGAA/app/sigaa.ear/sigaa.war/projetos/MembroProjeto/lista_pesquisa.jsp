<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>	
	<h2><ufrn:subSistema /> > Gerenciar Membros do Projeto</h2>
		
	<h:form id="form">
	
		<input type="hidden" name="lista_origem" id="lista_origem" value="${listaOrigem}"/>
		
		<div class="descricaoOperacao">
			<b>Caro coordenador</b>,
			<br />
			Existe uma grande diferença entre 'Finalizar um Membro da Projeto' e 'Excluir um Membro da Projeto.'
			Quando um membro é excluído, sua participação no Projeto é removida do sistema, portanto ele não terá o direito de 
			emitir Certificados e Declarações relacionados ao Projeto. 
			Quando um Membro do Projeto é 'Finalizado' ele não é removido do sistema e ainda é possível ao membro emitir o Certificado.  
		</div>
	
		<div class="infoAltRem">
			<h:graphicImage value="/img/extensao/businessman_add.png" style="overflow: visible;"/>: Cadastrar Novo Membro
			<h:graphicImage value="/img/extensao/businessman_view.png" style="overflow: visible;"/>: Visualizar
			<h:graphicImage value="/img/extensao/businessman_refresh.png" style="overflow: visible;"/>: Atualizar
			<h:graphicImage value="/img/extensao/businessman_delete.png" style="overflow: visible;"/>: Finalizar
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover		    
		</div>
		
		<table class="listagem">
			<caption class="listagem">Lista de Membros da Equipe Coordenadas pelo Usuário Atual</caption>
			<thead>
					<tr>
						<th>Nome</th>
						<th>Categoria</th>
						<th>Função</th>
						<th>Ch</th>
						<th>Início</th>
						<th>Fim</th>
						<th></th>							
						<th></th>							
						<th></th>
						<th></th>							
					</tr>
			</thead>
			<tbody>
				<c:set var="projeto" value=""/>
				<c:forEach items="#{membroProjeto.membrosProjetos}" var="item" varStatus="status">
					<c:if test="${ projeto != item.projeto.id }">
						<c:set var="projeto" value="${ item.projeto.id }"/>
						<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
							<td colspan="9" >
							 <html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?id=${item.projeto.id}&dispatch=view"> ${item.projeto.anoTitulo}</html:link>
							</td>
							<td width="2%">
								<h:commandLink action="#{membroProjeto.preAdicionarMembroEquipe}" style="border: 0;"	id="indicar_novo_membro_equipe">
							       <f:param name="id" value="#{item.projeto.id}"/>
					               <h:graphicImage url="/img/extensao/businessman_add.png" title="Cadastrar Novo Membro" />
								</h:commandLink>
							</td>
						</tr>
					</c:if>
				
	              		<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.pessoa.nome}</td>
						<td>${item.categoriaMembro.descricao}</td>
						<c:set var="cor" value="${item.coordenador ? 'red':'black'}" />
						<td><font color="${cor}">${item.funcaoMembro.descricao}</font></td>
						<td>${item.chDedicada > 0 ? item.chDedicada : '-'}${item.chDedicada > 0 ? 'h' : ''}</td>
						<td><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></td>									
						<td><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></td>
						
						<td width="2%">								               
							<h:commandLink action="#{membroProjeto.view}" style="border: 0;" id="ver_dados_membro_equipe">
							   <f:param name="idMembro" value="#{item.id}"/>
					           <h:graphicImage url="/img/extensao/businessman_view.png" title="Visualizar"/>
							</h:commandLink>
						</td>
						
						<td width="2%">
							<h:commandLink action="#{membroProjeto.preAlterarMembroEquipe}" style="border: 0;" id="alterar_membro_equipe" >
						       <f:param name="idMembro" value="#{item.id}"/>
				               <h:graphicImage url="/img/extensao/businessman_refresh.png" title="Atualizar" />
							</h:commandLink>
						</td>
						
						<td width="2%">
							<h:commandLink action="#{membroProjeto.preRemoverMembroEquipe}" style="border: 0;" id="finalizar_membro_equipe">
						       <f:param name="idMembro" value="#{item.id}"/>
				               <h:graphicImage url="/img/extensao/businessman_delete.png" title="Finalizar"/>
							</h:commandLink>
						</td>
						
						<td width="2%">
							<h:commandLink action="#{membroProjeto.preInativar}" style="border: 0;" id="remover_membro_equipe" onclick="return confirm('Tem certeza que deseja remover este membro da Equipe?');">
						       <f:param name="id" value="#{item.id}"/>
				               <h:graphicImage url="/img/delete.gif" title="Remover"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
		 		   
				<c:if test="${empty membroProjeto.membrosProjetos}" >
						<tr><td colspan="4" align="center"><font color="red">Não há membros de equipe cadastrados em ações ativas coordenadas pelo usuário atual.</font></td></tr>
				</c:if>
			</tbody>		
		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>