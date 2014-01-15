<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<a4j:keepAlive beanName="termoPublicacaoTD" />
	<h2>
		<ufrn:subSistema /> > Lista de Solicitações
		<c:if test="${requestScope.publicacao}">
			Pendentes
		</c:if>
	</h2>
	
	<c:if test="${termoPublicacaoTD.portalDiscente}">
		<div class="descricaoOperacao">
			<p>Caro Discente,</p><br/>
			<p>
				Através desta operação será possível visualizar as Solicitações de publicação de <b>Teses/Dissertações</b>.
			</p>
			<p>
				A Solicitação será analisada pelo seu Orientador, sendo Reprovado, será necessário que solicite um novo Termo de Publicação 
				para uma nova análise. Uma vez Aprovado, o setor responsável pela catalogação e publicação na BDTD, realizará a publicação do referido trabalho.
			</p>			
		</div>
	</c:if>
	<c:if test="${termoPublicacaoTD.portalDocente}">
		<div class="descricaoOperacao">
			<p>Caro Usuário(a),</p><br/>
			<p>
				Através desta operação será possível visualizar todas as solicitações de publicação de <b>Teses/Dissertações dos seus Orientandos</b>, 
				bem como <b>Aprovar</b> ou <b>Reprovar</b> o trabalho enviado. 						
			</p>
			<p>
				Sendo Reprovado, será necessário que o discente solicite um novo Termo de Publicação para uma nova análise. 
				Uma vez Aprovado, o setor responsável pela catalogação e publicação na BDTD, realizará a publicação do referido trabalho.
			</p>
		</div>
	</c:if>
	<c:if test="${requestScope.publicacao}">
		<div class="descricaoOperacao">
			<p>Caro Usuário(a),</p><br/>
			<p>
				Através desta operação será possível visualizar e publicar todas as solicitações de publicação de Teses/Dissertações na BDTD Pendentes. 
			</p>
		</div>
	</c:if>	
	
	<center>
		<div class="infoAltRem">			
			<h:graphicImage value="/img/graduacao/coordenador/documento.png" style="overflow: visible;"/>: Visualizar Termo
			<h:graphicImage value="../../../shared/img/icones/download.png" style="overflow: visible;"/>: Download do Arquivo		
			<c:if test="${termoPublicacaoTD.portalDocente}">
				<h:graphicImage value="/img/check.png" style="overflow: visible;"/>: Aprovar Trabalho
				<h:graphicImage value="/img/delete.png" style="overflow: visible;"/>: Reprovar Trabalho
			</c:if>
			<c:if test="${requestScope.publicacao}">
				<h:graphicImage value="/img/biblioteca/enderecoEletronico.png" style="overflow: visible;"/>: Publicar
			</c:if>
			<c:if test="${termoPublicacaoTD.portalCoordenadorStricto || termoPublicacaoTD.portalPpg}">
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Termo
			</c:if>
		</div>
	</center>	
	
	<h:form id="form">
		<table class="listagem">
			<caption>Lista de Solicitações de Publicação de Teses/Dissertações ( ${fn:length(termoPublicacaoTD.listagemTermos)} )</caption>
			<thead>
				<tr>
					<th style="text-align: center;">Data da Solicitação</th>
					<th>Discente</th>
					<th>Tipo Publicação</th>
					<th style="text-align: center;">Data para Publicação</th>
					<th style="text-align: center;">Status</th>
					<th colspan="6"></th>
				</tr>
			</thead>
			<c:if test="${empty termoPublicacaoTD.listagemTermos}">		
				<tr>
					<td colspan="8" align="center"><i>Nenhum Solicitação Pendente.</i></td>
				</tr>
			</c:if>			
			<c:set var="Idcurso" value="0"/>
			<c:set var="nivel" value=""/>
			<c:if test="${not empty termoPublicacaoTD.listagemTermos}">		
				<c:forEach items="#{termoPublicacaoTD.listagemTermos}" var="linha" varStatus="loop">
					<c:if test="${Idcurso != linha.discente.gestoraAcademica.id || nivel != linha.discente.nivel}">
						<tr>
							<td colspan="11" style="background-color: #C8D5EC;">
								<c:set var="Idcurso" value="#{linha.discente.discente.gestoraAcademica.id}"/>
								<c:set var="nivel" value="#{linha.discente.discente.nivel}"/>
								<b>${linha.discente.gestoraAcademica.nome} - ${linha.discente.nivelDesc}</b>
							</td>
						</tr>		
					</c:if>	
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align: center;">
							<ufrn:format type="data" valor="${linha.criadoEm}"/>
						</td>
						<td>${linha.discente.matriculaNome}</td>
						<td>
							<c:if test="${linha.parcial}">
								Parcial	
							</c:if>
							<c:if test="${!linha.parcial}">
							    Total
							</c:if>
						</td>						
						<td style="text-align: center;">
							<ufrn:format type="data" valor="${linha.dataPublicacao}"/>
						</td>
						<td style="text-align: center;">
							<c:choose>
								<c:when test="${not empty linha.banca.dadosDefesa.linkArquivo}">
									<span style="color:green; font-weight: bold;">PUBLICADO</span>
								</c:when>
								<c:when test="${linha.reprovado}">
									<span style="color:red; font-weight: bold;">${linha.descricaoStatus}</span>
								</c:when>
								<c:when test="${linha.reprovado}">
									<span style="color:green; font-weight: bold;">${linha.descricaoStatus}</span>
								</c:when>
								<c:otherwise>
									${linha.descricaoStatus}
								</c:otherwise>						
							</c:choose>		
						</td>
						<td>
							<c:if test="${termoPublicacaoTD.portalDocente || requestScope.publicacao || linha.aprovado}">
								<h:commandButton image="/img/graduacao/coordenador/documento.png" title="Visualizar Termo" id="btEmissaoTermo"
										action="#{termoPublicacaoTD.emitirTEDE}" styleClass="noborder">
										<f:setPropertyActionListener value="#{linha}" target="#{termoPublicacaoTD.obj}"/>
								</h:commandButton>						
							</c:if>
						</td>
						<td>
							<c:if test="${not empty linha.banca.dadosDefesa.idArquivo}">
								<html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${linha.banca.dadosDefesa.idArquivo}">
									<img src="/shared/img/icones/download.png" title="Download do Arquivo"/>
								</html:link>
							</c:if>									
						</td> 
						<td>
							<c:if test="${requestScope.publicacao == true}">
								<h:commandButton image="/img/biblioteca/enderecoEletronico.png" title="Publicar" id="btaoPublicar"
										action="#{termoPublicacaoTD.publicar}" styleClass="noborder">
										<f:setPropertyActionListener value="#{linha}" target="#{termoPublicacaoTD.obj}"/>
								</h:commandButton>							
							</c:if>
							<c:if test="${not empty linha.banca.dadosDefesa.linkArquivo && !requestScope.publicacao == true}">
								<h:commandLink target="_BLANK"  action="#{termoPublicacaoTD.visualizarPublicacao}" title="Visualizar Publicação" id="verPublicacao">
									<h:graphicImage  value="/shared/img/icones/download.png"/>
									<f:setPropertyActionListener value="#{linha}" target="#{termoPublicacaoTD.obj}"/>
								</h:commandLink>						
							</c:if>
						</td>
						<td>						
							<c:if test="${termoPublicacaoTD.portalCoordenadorStricto || termoPublicacaoTD.portalPpg}">
								<h:commandButton image="/img/alterar.gif" title="Alterar Termo" id="btnAlterar"
										action="#{termoPublicacaoTD.alterar}" styleClass="noborder">
										<f:setPropertyActionListener value="#{linha}" target="#{termoPublicacaoTD.obj}"/>
								</h:commandButton>							
							</c:if>
						</td>														
						<td>
							<c:if test="${termoPublicacaoTD.portalDocente && !linha.aprovado}">
								<h:commandButton image="/img/check.png" title="Aprovar Trabalho" id="btaoaprovarTermo"
										action="#{termoPublicacaoTD.aprovarTermo}" styleClass="noborder">
										<f:setPropertyActionListener value="#{linha}" target="#{termoPublicacaoTD.obj}"/>
								</h:commandButton>				
							</c:if>																		
						</td>	
						<td>
							<c:if test="${termoPublicacaoTD.portalDocente && !linha.aprovado}">
								<h:commandButton image="/img/delete.png" title="Reprovar Trabalho" id="btnRemTermo"
										action="#{termoPublicacaoTD.reprovarTermo}" styleClass="noborder">
										<f:setPropertyActionListener value="#{linha}" target="#{termoPublicacaoTD.obj}"/>
								</h:commandButton>				
							</c:if>																
						</td>					
					</tr>				
				</c:forEach>
			</c:if>
		</table>
	</h:form>		
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
