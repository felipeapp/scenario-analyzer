<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> &gt; Documentos autenticados de Extensão</h2>

	<div class="descricaoOperacao">
		<b>Atenção:</b><br/>
		Os documentos só poderão ser emitidos para Membros da Equipe ativos.<br/>
		Os Certificados só serão liberados quando a participação do membro da equipe na ação for finalizada.<br>
		As Declarações poderão ser emitidas a qualquer tempo para os membros ativos da ação de extensão.<br/>
		Nos casos de participação como Discente de Extensão os Certificados só serão liberados quando o discente enviar o Relatório Final.<br>
	</div>

	
<h:form id="form">

	<div class="infoAltRem">
		<h:graphicImage value="/img/extensao/businessman_view.png" style="overflow: visible;"/>: Visualizar
		<h:graphicImage value="/img/comprovante.png" height="19" width="19" style="overflow: visible;"/>: Declaração		    
		<h:graphicImage value="/img/certificate.png" height="19" width="19" style="overflow: visible;"/>: Certificado		    		    
	</div>
	
	
		<table class="listagem">
				<caption class="listagem">Lista de participações como membro de equipe organizadora das Ações de Extensão</caption>
				<thead>
						<tr>
							<th width="50%">Ação de Extensão</th>
							<th>Categoria</th>
							<th>Função</th>
							<th style="text-align: center;" >Início</th>
							<th style="text-align: center;" >Fim</th>
							<th></th>
							<th></th>							
							<th></th>							
						</tr>
				</thead>
				<tbody>
						
					<c:forEach items="#{documentosAutenticadosExtensao.membros}" var="item" varStatus="status">						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${item.projeto.anoTitulo }</td>
									<td>${item.categoriaMembro.descricao}</td>
									<td><font color=${item.coordenador ? 'red':'black'}>${item.funcaoMembro.descricao}</font></td>
									<td style="text-align: center;" ><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></td>
									<td style="text-align: center;" ><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></td>

									<td width="2%">								               
										<h:commandLink action="#{membroProjeto.view}" style="border: 0;" id="view_membro_equipe_">
										   <f:param name="idMembro" value="#{item.id}"/>
								           <h:graphicImage url="/img/extensao/businessman_view.png" alt="Ver Membro Equipe"/>
										</h:commandLink>
									</td>	
									
									<td width="2%">
										<h:commandLink title="Emitir declaração" action="#{declaracaoExtensaoMBean.emitirDeclaracaoMembroProjeto}" 
											immediate="true"  rendered="#{item.passivelEmissaoDeclaracao}" id="emitir_declaracao_membro_equipe_">
										   <f:setPropertyActionListener target="#{declaracaoExtensaoMBean.membro.id}" value="#{item.id}"/>
					                   	   <h:graphicImage url="/img/comprovante.png" height="19" width="19" alt="Emitir Declaração"/>
										</h:commandLink>
									</td>

									<td width="2%">
										<h:commandLink title="Emitir certificado" action="#{certificadoExtensaoMBean.emitirCertificadoMembroProjeto}" 
											immediate="true" rendered="#{item.passivelEmissaoCertificado}" id="emitir_certificado_membro_equipe_">
											<f:setPropertyActionListener target="#{certificadoExtensaoMBean.membro.id}" value="#{item.id}"/>
					                   		<h:graphicImage url="/img/certificate.png" height="19" width="19" alt="Emitir Certificado"/>
										</h:commandLink>
									</td>									
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty documentosAutenticadosExtensao.membros}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">Usuário atual não participa como membro de equipe organizadora de ações de extensão.</font></td></tr>
			 		   </c:if>
			 		   
					</tbody>	
					
		</table>
		
		<br/>
		<br/>
		
		
		<table class="listagem">
				<caption class="listagem">Lista de participações como público alvo das Ações de Extensão</caption>
				<thead>
						<tr>
							<th width="50%">Ação de Extensão</th>
							<th>Categoria</th>
							<th>Participação</th>
							<th style="text-align: center;">Data do Cadastro</th>
							<th style="text-align: right;">Frequência</th>							
							<th></th>
							<th></th>							
							<th></th>							
						</tr>
				</thead>
				<tbody>
						
					<c:forEach items="#{documentosAutenticadosExtensao.participantes}" var="item" varStatus="status">						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									
									
									<td>${item.codigoTitulo}</td>
									<td>${item.tipoParticipanteString}</td>
									<td>${item.tipoParticipacao.descricao}</td>
									<td style="text-align: center;"><fmt:formatDate value="${item.dataCadastro}" pattern="dd/MM/yyyy" /></td>
									<td style="text-align: right;">${item.frequencia}%</td>
									
									<td width="2%">								               
										<h:commandLink action="#{gerenciarInscritosCursosEEventosExtensaoMBean.visualizarDadosParticipante}" style="border: 0;" id="view_participante_">
										   <f:param name="idCadastroParticipante" value="#{item.cadastroParticipante.id}"/>
								           <h:graphicImage url="/img/extensao/businessman_view.png" alt="Ver Participante"/>
										</h:commandLink>
									</td>	
									
									<td width="2%">
										<h:commandLink title="Emitir declaração" action="#{declaracaoExtensaoMBean.emitirDeclaracaoParticipante}" 
											immediate="true" rendered="#{item.passivelEmissaoDeclaracaoParticipante}" id="emitir_declaracao_participante_">
										   <f:setPropertyActionListener target="#{declaracaoExtensaoMBean.participante.id}" value="#{item.id}"/>
					                   	   <h:graphicImage url="/img/comprovante.png" height="19" width="19" alt="Emitir Declaração Participante"/>
										</h:commandLink>
									</td>

									<td width="2%">
										<h:commandLink title="Emitir certificado" action="#{certificadoExtensaoMBean.emitirCertificadoParticipante}" 
											immediate="true" rendered="#{item.passivelEmissaoCertificadoParticipante}" id="emitir_certificado_participante_">
											<f:setPropertyActionListener target="#{certificadoExtensaoMBean.participante.id}" value="#{item.id}"/>
					                   		<h:graphicImage url="/img/certificate.png" height="19" width="19" alt="Emitir Certificado Participante"/>
										</h:commandLink>
									</td>								
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty documentosAutenticadosExtensao.participantes}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">Usuário atual não participa como público alvo de ações de extensão.</font></td></tr>
			 		   </c:if>
			 		   
					</tbody>	
					
		</table>


		<br/>
		<br/>
		
		
		<table class="listagem">
				<caption class="listagem">Lista de participações como discente das Ações de Extensão</caption>
				<thead>
						<tr>
							<th width="50%">Ação de Extensão</th>
							<th>Vínculo</th>
							<th style="text-align: center;">Início</th>
							<th style="text-align: center;">Fim</th>
							<th>Situação</th>
							<th></th>
							<th></th>							
							<th></th>							
							<th></th>
						</tr>
				</thead>
				<tbody>
						
					<c:forEach items="#{documentosAutenticadosExtensao.discentesExtensao}" var="item" varStatus="status">						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${item.atividade.codigoTitulo}</td>
									<td>${item.tipoVinculo.descricao}</td>
									<td style="text-align: center;"><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></td>
									<td style="text-align: center;"><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></td>
									<td>${item.situacaoDiscenteExtensao.descricao}</td>
									
									<td width="2%">								               
										<h:commandLink action="#{discenteExtensao.view}" style="border: 0;" id="view_discente_extensao_">
										   <f:param name="idDiscenteExtensao" value="#{item.id}"/>
								           <h:graphicImage url="/img/extensao/businessman_view.png" alt="Ver Discente Extesão"/>
										</h:commandLink>
									</td>
									
									<td width="2%">
										<h:commandLink title="Emitir certificado" action="#{certificadoExtensaoMBean.emitirCertificadoDiscenteExtensao}" 
											 rendered="#{item.passivelEmissaoCertificado}" id="emitir_certificado_discente_">
											<f:setPropertyActionListener target="#{certificadoExtensaoMBean.discenteExtensao.id}" value="#{item.id}"/>
					                   		<h:graphicImage url="/img/certificate.png" height="19" width="19" alt="Emitir Certificado Discente"/>
										</h:commandLink>
									</td>			
									
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty documentosAutenticadosExtensao.discentesExtensao}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">Usuário atual não participa ou participou como discente de ações de extensão.</font></td></tr>
			 		   </c:if>
			 		   
				</tbody>	
					
		</table>


		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>