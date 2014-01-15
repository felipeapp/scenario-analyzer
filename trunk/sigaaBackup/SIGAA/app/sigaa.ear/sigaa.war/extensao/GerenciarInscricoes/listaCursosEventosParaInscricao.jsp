<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">

.linhaParAtividade{
	background: #b5c7e5;
	font-weight: bold;
}

.linhaImparAtividade{
	background: #DBE3F3;
	font-weight: bold;
}


</style>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />
	
	<h2><ufrn:subSistema /> &gt; Gerenciar Períodos de Inscrições </h2>
	
	<h:form id="formListaCursosEventoParaInscricao">
	
		<div class="descricaoOperacao">
				<p> Caro(a) Usuário(a), </p>
				<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.projetoSelecionarGestor == null}">
					<p> Abaixo são apresentadas as ações de extensão ativas <strong>em execução</strong>, nas quais o(a) senhor(a) é coordenador e cujos os períodos de inscrições podem ser abertos. </p>
				</c:if>
				<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.projetoSelecionarGestor != null}">
					<p> Abaixo são apresentadas as ações de extensão ativas <strong>em execução</strong>, do projeto selecionado e cujos os períodos de inscrições podem ser abertos. </p>
				</c:if>
				
				<p> A partir do momento em que as inscrições forem criadas, a ação estará visível na área pública do sistema para que os usuários possam se inscrever.</p>
				<br/>
				<p> <strong>IMPORTANTE:</strong> Caso uma Atividade possua  Mini Atividades Vinculadas, devem ser abertas inscrições para cada Mini Atividade listada, 
				independente da inscrição da atividade a qual ela pertence. <br/>
				</p>
				<br/>
				<p>
				<strong>O usuário só poderá se inscrever na mini atividade se ele se inscrever na atividade principal.</strong>
				</p>
				<p>
					<ul>
						<li> <strong>Previsão de Vagas:</strong> Previsão de Vagas que foi informada no Cadastro de Curso ou Evento.</li>
						<li> <strong>Vagas Abertas:</strong> Número de Vagas Abertas nas Inscrições</li>
						<li> <strong>Inscritos Aprovados:</strong> Número de inscrições aprovadas para o curso ou evento.</li>
						<li> <strong>Vagas Disponíveis:</strong> Número de Vagas ainda disponíveis. ( "Vagas Abertas" - "Inscrições Aprovadas" )</li>
					</ul>
				</p>
				
				<br/>
				<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.projetoSelecionarGestor == null}">
					<p> Para listar todas as ações de extensão das quais o senhor (a) faz parte, utilize a opção: 
						<i> <h:commandLink value="Listar Minhas Ações" action="#{atividadeExtensao.listarMinhasAtividades}" /> </i>
					</p>
				</c:if>
		</div>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/clipboard.gif" height="16" width="16" style="overflow: visible;" />: Gerenciar Períodos de Inscrições
			<h:graphicImage value="/img/user.png" height="16" width="16" style="overflow: visible;" />: Gerenciar Inscritos
		    <br/>
		</div>
		
		
		<table id="tabelaAtividades"  class="listagem" style="width: 100%;">

				<caption>Lista dos Cursos e Eventos para inscrições na Área Pública</caption>
				<thead>
					<tr>
						<th width="10%">Código </th>
						<th colspan="2" width="50%">Título </th>
						<th style="text-align: right;">Previsão de Vagas</th>
						<th style="text-align: right;">Vagas Abertas</th>
						<th style="text-align: right;">Inscritos Aprovados</th>
						<th style="text-align: right;">Vagas Disponíveis</th>
						<th style="width: 1%;" ></th>
						<th style="width: 1%;" ></th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.qtdCursosEventosParaInscricao > 0 }">
						<c:forEach items="#{gerenciarInscricoesCursosEventosExtensaoMBean.cursosEventosParaInscricao}" var="cursoEvento" varStatus="count">
							<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
								<td>${cursoEvento.codigo}</td>
								<td colspan="2" style="font-weight: bold;">${cursoEvento.titulo}</td>
								<td style="text-align:right;">${cursoEvento.cursoEventoExtensao.numeroVagas}</td> <%-- Vagas informadas no momento da inscrição--%>
								<td style="text-align:right;">${cursoEvento.numeroVagasAbertas}</td>
								<td style="text-align:right;">${cursoEvento.numeroInscritos}</td>
								<td style="text-align:right;">${cursoEvento.numeroVagasAbertas - cursoEvento.numeroInscritos}</td>
								<td style="text-align: center;">
									<h:commandLink id="cmdLinkInscricaoAtividade" title="Gerenciar Períodos de Inscrições" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarInscricaoAtividades}">
										<f:param name="idAtividadeSelecionada" value="#{cursoEvento.id}" />
										<h:graphicImage url="/img/clipboard.gif" height="16" width="16" />
									</h:commandLink>
								</td>
								<td style="text-align: center;">
									<h:commandLink id="cmdLinkInscritosAtividade" title="Gerenciar Inscritos" action="#{gerenciarInscritosCursosEEventosExtensaoMBean.listarInscritosAtividade}">
										<f:param name="idAtividadeSelecionada" value="#{cursoEvento.id}" />
										<h:graphicImage url="/img/user.png" />
									</h:commandLink>
								</td>
							</tr>
							
							<c:if test="${cursoEvento.qtdSubAtividadesExtensao > 0 }">
								<c:forEach items="#{cursoEvento.subAtividadesExtensao}" var="miniAtividade" varStatus="status">
									<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
										<td> </td>
										<td>${miniAtividade.titulo}</td>
										<td>${miniAtividade.tipoSubAtividadeExtensao.descricao}</td>
										<td> </td>
										<td style="text-align:right;">${miniAtividade.numeroVagasAbertas}</td>
										<td style="text-align:right;">${miniAtividade.numeroInscritos}</td>
										<td style="text-align:right;">${miniAtividade.numeroVagasAbertas - miniAtividade.numeroInscritos}</td>
										<td style="text-align: center;">
											<h:commandLink 	id="cmdLinkInscricaoMiniAtividade" title="Gerenciar Períodos de Inscrições" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarInscricaoSubAtividades}">
												<f:param name="idSubAtividadeSelecionada" value="#{miniAtividade.id}" />
												<h:graphicImage url="/img/clipboard.gif" height="16" width="16"/>
											</h:commandLink>
										</td>
										<td style="text-align: center;">
											<h:commandLink 	id="cmdLinkInscritosMiniAtividade" title="Gerenciar Inscritos" action="#{gerenciarInscritosCursosEEventosExtensaoMBean.listarInscritosSubAtividade}">
												<f:param name="idSubAtividadeSelecionada" value="#{miniAtividade.id}" />
												<h:graphicImage url="/img/user.png"/>
											</h:commandLink>
										</td>
										
										
									</tr>
								</c:forEach>
							</c:if>
							
							
						</c:forEach>
					</c:if>
					<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.qtdCursosEventosParaInscricao == 0 }">
						<tr>
							<td style="color: red; text-align: center;" colspan="8">Não Existem Cursos ou Eventos Disponíveis para Inscrição </td>
						</tr>
					</c:if>
				</tbody>
			
		</table>			
		
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>