<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Equipe Responsável pela Ação Integrada</h2>
		
	<h:form id="form">
	
		<input type="hidden" name="lista_origem" id="lista_origem" value="${listaOrigem}"/>
		
		<div class="descricaoOperacao">
			<b>Caro coordenador</b>,
			<br />
			Existe uma grande diferença entre 'Finalizar um Membro da Equipe' e 'Excluir um Membro da Equipe.'
			Quando um membro é excluído da sua participação na Ação Integrada é removida do sistema, portanto ele não terá o direito de 
			emitir Certificados e Declarações relacionados à Ação Integrada. 
			Quando um Membro da Equipe é 'Finalizado' ele não é removido do sistema e ainda é possível ao membro emitir o Certificado.  
		</div>
	
		<div class="infoAltRem">
			<h:graphicImage value="/img/extensao/businessman_add.png" style="overflow: visible;"/>: Cadastrar Novo Membro
			<h:graphicImage value="/img/coordenador.png" style="overflow: visible;" />: Alterar Coordenador
			<h:graphicImage value="/img/printer_ok.png" style="overflow: visible;" width="18px" height="18px"/>: Imprimir
			<h:graphicImage value="/img/extensao/businessman_refresh.png" style="overflow: visible;"/>: Atualizar
			<h:graphicImage value="/img/extensao/businessman_delete.png" style="overflow: visible;"/>: Finalizar
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover		    
			<br />		    
			<h:graphicImage value="/img/comprovante.png" height="19" width="19" style="overflow: visible;"/>: Emitir Declaração
			<h:graphicImage value="/img/certificate.png" height="17" width="17" style="overflow: visible;"/>: Emitir Certificado
		</div>
		
			<table class="listagem">
					<caption class="listagem">Lista de Membros da Equipe de Ações Ativas Coordenadas pelo Usuário Atual</caption>
					<thead>
							<tr>
								<th>Nome</th>
								<th>Categoria</th>
								<th>Função</th>
								<th style="text-align: right;">Ch</th>
								<th style="text-align: center;">Início</th>
								<th style="text-align: center;">Fim</th>
								<th></th>							
								<th></th>							
								<th></th>
								<th></th>							
								<th></th>							
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
										<td colspan="14">${item.projeto.anoTitulo}</td>
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
										<c:set var="cor" value="${ item.coordenadorProjeto ? 'red' : 'black' }" />
										<td><font color="${cor}">${item.funcaoMembro.descricao}</font></td>
										<td style="text-align: right;">${item.chDedicada} h</td>
										<td style="text-align: center;"><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></td>									
										<td style="text-align: center;"><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></td>
										
										<td width="2%">
											<h:commandLink action="#{membroProjeto.preAlterarCoordenador}" style="border: 0;" id="alterarCoordenador" rendered="#{item.coordenadorProjeto}">
												<f:param name="idMembro" value="#{item.id}"/>
												<f:param name="idProjeto" value="#{item.projeto.id}"/>
												<h:graphicImage url="/img/coordenador.png" title="Alterar Coordenador" />
											</h:commandLink>
										</td>									
										
										<td width="2%">								               
											<h:commandLink action="#{membroProjeto.view}" style="border: 0;" id="ver_dados_membro_equipe">
											   <f:param name="idMembro" value="#{item.id}"/>
									           <h:graphicImage url="/img/printer_ok.png" width="18px" height="18px" title="Imprimir" />
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
																											
										<td width="2%" align="right">
											<h:commandLink title="Emitir declaração" action="#{declaracaoExtensaoMBean.emitirDeclaracaoMembroProjeto}" immediate="true" id="emitir_declaracao_membro_equipe" 
											  rendered="#{item.passivelEmissaoDeclaracao && !membroProjeto.associados}">
											   <f:setPropertyActionListener target="#{declaracaoExtensaoMBean.membro.id}" value="#{item.id}"/>
						                   	   <h:graphicImage url="/img/comprovante.png" height="19" width="19"/>
											</h:commandLink>
										</td>

										<td width="2%" align="right">
											<h:commandLink title="Emitir declaração" action="#{ declaracaoAssociadosMBean.emitirDeclaracao }" immediate="true" id="emitir_declaracao_membro_equipe_associados" 
											  rendered="#{item.passivelEmissaoDeclaracao && membroProjeto.associados}">
											   <f:setPropertyActionListener target="#{ declaracaoAssociadosMBean.membro.id}" value="#{ item.id }"/>
						                   	   <h:graphicImage url="/img/comprovante.png" height="19" width="19"/>
											</h:commandLink>
										</td>
										
										<td width="2%">
											<h:commandLink title="Emitir certificado" action="#{certificadoExtensaoMBean.emitirCertificadoMembroProjeto}" immediate="true" id="emitir_certificado_membro_equipe" 
											  rendered="#{(item.passivelEmissaoCertificado && !membroProjeto.associados)}">
												<f:setPropertyActionListener target="#{certificadoExtensaoMBean.membro.id}" value="#{item.id}"/>
						                   		<h:graphicImage url="/img/certificate.png" height="19" width="19"/>
											</h:commandLink>
										</td>
										
										<td width="2%">
											<h:commandLink title="Emitir Certificado" action="#{declaracaoMembroProjIntegradoMBean.emitir}" immediate="true" 
												id="emitir_certificado_membro_equipe_associados" rendered="#{membroProjeto.associados && item.projeto.concluido }">
												<f:setPropertyActionListener target="#{declaracaoMembroProjIntegradoMBean.obj.id}" value="#{ item.id }"/>
						                   		<h:graphicImage url="/img/certificate.png" height="19" width="19"/>
											</h:commandLink>
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