<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> &gt; Equipe Respons�vel pelo Projeto</h2>
	
<h:form id="form">

	<input type="hidden" name="lista_origem" id="lista_origem" value="${listaOrigem}"/>
	<div class="descricaoOperacao">
		<b>Caro coordenador</b>,
		<br/>
		Existe uma grande diferen�a entre 'Finalizar um Membro da Equipe' e 'Excluir um Membro da Equipe.'
		Quando um membro � exclu�do, sua participa��o no Projeto � removida do sistema, portanto ele n�o ter� o direito de 
		emitir Certificados e Declara��es relacionados ao Projeto. 
		Quando um Membro da Equipe � 'Finalizado' ele n�o � removido do sistema e ainda � poss�vel ao membro emitir o Certificado.  
	</div>

	<div class="infoAltRem">
		<h:graphicImage value="/img/extensao/businessman_add.png" style="overflow: visible;"/>: Cadastrar Novo
		<h:graphicImage value="/img/extensao/businessman_delete.png" style="overflow: visible;"/>: Finalizar
		<h:graphicImage value="/img/extensao/businessman_refresh.png" style="overflow: visible;"/>: Atualizar
		<h:graphicImage value="/img/extensao/businessman_view.png" style="overflow: visible;"/>: Visualizar
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Membro		    
	</div>
	
		<table class="listagem">
				<caption class="listagem">Lista de Membros da Equipe do Projeto</caption>
				<thead>
						<tr>
							<th>Nome</th>
							<th>Categoria</th>
							<th>Fun��o</th>
							<th>Ch</th>
							<th>In�cio</th>
							<th>Fim</th>
							<th></th>							
							<th></th>							
							<th></th>
							<th></th>
							<th></th>							
							<th></th>							
						</tr>
				</thead>
				<tbody>
						
					<c:set var="idProjeto" value="0"/>
					<c:forEach items="#{membroProjeto.membrosProjetos}" var="membro" varStatus="status">
						
							<c:if test="${ idProjeto != membro.projeto.id }">
								<c:set var="idProjeto" value="${ membro.projeto.id }"/>
								
								<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
									<td colspan="10" >${membro.projeto.titulo}</td>
									<td width="2%">
										<h:commandLink action="#{membroProjeto.preAdicionarMembroEquipe}" style="border: 0;"	id="indicar_novo_membro_equipe">
									       <f:param name="id" value="#{membro.projeto.id}"/>
							               <h:graphicImage url="/img/extensao/businessman_add.png" title="Inserir Membro na Equipe" />
										</h:commandLink>
									</td>
								</tr>
							</c:if>		
						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImembroar" }">
									<td>${membro.pessoa.nome}</td>
									<td>${membro.categoriaMembro.descricao}</td>
									<c:set var="cor" value="${membro.coordenador ? 'red':'black'}" />
									<td><font color="${cor}">${membro.funcaoMembro.descricao}</font></td>
									<td>${membro.chDedicada}h</td>
									<td><fmt:formatDate value="${membro.dataInicio}" pattern="dd/MM/yyyy" /></td>									
									<td><fmt:formatDate value="${membro.dataFim}" pattern="dd/MM/yyyy" /></td>
									
									<td width="2%">
										<h:commandLink action="#{membroProjeto.preRemoverMembroEquipe}" style="border: 0;" id="finalizar_membro_equipe">
									       <f:param name="idMembro" value="#{membro.id}"/>
							               <h:graphicImage url="/img/extensao/businessman_delete.png" title="Finalizar Membro da Equipe"/>
										</h:commandLink>
									</td>
									
									
									<td width="2%">
										<h:commandLink action="#{membroProjeto.preAlterarMembroEquipe}" style="border: 0;" id="alterar_membro_equipe" >
									       <f:param name="idMembro" value="#{membro.id}"/>
							               <h:graphicImage url="/img/extensao/businessman_refresh.png" title="Alterar Membro da Equipe" />
										</h:commandLink>
									</td>
																										
									<td width="2%">								               
										<h:commandLink action="#{membroProjeto.view}" style="border: 0;" id="ver_dados_membro_equipe">
										   <f:param name="idMembro" value="#{membro.id}"/>
								           <h:graphicImage url="/img/extensao/businessman_view.png" />
										</h:commandLink>
									</td>	

									<td>&nbsp;</td>
									
									<td width="2%">
										<h:commandLink action="#{membroProjeto.preInativar}" style="border: 0;" id="remover_membro_equipe" onclick="return confirm('Tem certeza que deseja remover este membro da A��o de Extens�o?');">
									       <f:param name="id" value="#{membro.id}"/>
							               <h:graphicImage url="/img/delete.gif" title="Remover Membro da Equipe"/>
										</h:commandLink>
									</td>
									
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty membroProjeto.membrosProjetos}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">N�o h� membros de equipe cadastrados em a��es ativas coordenadas pelo usu�rio atual.</font></td></tr>
			 		   </c:if>
			 		   
					</tbody>	
					
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>