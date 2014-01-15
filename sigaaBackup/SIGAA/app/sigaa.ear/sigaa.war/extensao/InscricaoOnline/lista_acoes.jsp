<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">

table.listagem tr.atividadePaiSubAtividade td{
	background: #C8D5EC;
	font-weight: bold;
	padding-left: 20px;
}

</style>

<f:view>
	<h:messages showDetail="true"/>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h2><ufrn:subSistema /> &gt; Abrir/Alterar Inscri��es On-line</h2>
	
	
	<h:form id="form">
	
		<div class="descricaoOperacao">
				<p> Caro Usu�rio, </p>
				<p> Abaixo s�o apresentadas as a��es de extens�o ativas em execu��o, nas quais o(a) senhor(a) � coordenador e cujas inscri��es podem ser criadas. </p>
				<p> A partir do momento em que as inscri��es forem criadas, a a��o estar� vis�vel na �rea p�blica do sistema para que os usu�rios possam se inscrever.</p>
				<br/>
				<p> <strong>IMPORTANTE:</strong> As mini atividades vinculadas as atividades de extens�o s�o mostradas em uma listagem separada. 
				Para que os usu�rios consigam se inscriver em uma Mini Atividade devem ser abertas inscri��es para cada Mini Atividade listada, independete da inscri��o da atividade a qual ela pertence. </p>
				<br/>
				<p> Para listar todas a��es de extens�o das quais o senhor(a) faz parte, utilize a op��o: 
					<i> <h:commandLink value="Listar Minhas A��es" action="#{atividadeExtensao.listarMinhasAtividades}" /> </i>
				</p>
		</div>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/refresh.png" style="overflow: visible;" />: Alterar N�mero de Vagas da A��o
			<h:graphicImage value="/img/clipboard.gif" height="20" width="20" style="overflow: visible;" />: Gerenciar Inscri��es
		    <br/>
		</div>

	
	
		<c:set var="atividades" value="#{inscricaoAtividade.atividadesCoordenador}" />
		<c:choose>
			<c:when test="${not empty atividades}">
			
				<table id="tabelaAtividades" width="90%" class="listagem">

					<caption>Lista dos Cursos e Eventos para inscri��o na �rea P�blica</caption>
					<thead>
						<tr>
							<th width="10%">C�digo </th>
							<th width="50%">T�tulo </th>
							<th style="text-align: right;">Previs�o de N� de Vagas Oferecidas</th>
							<th style="text-align: right;">Vagas Abertas nas Inscri��es</th>
							<th style="text-align: right;">N�m. Inscritos</th>
							<th style="text-align: right;">Vagas Dispon�veis</th>
							<th colspan="1" style="width: 1%;" />
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{atividades}" var="atividade" varStatus="count">
						<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${atividade.codigo}</td>
							<td>${atividade.titulo}</td>
							<td width="10%" align="right">
									<h:inputText value="#{atividade.cursoEventoExtensao.numeroVagas}" immediate="true" id="numVagas"  label="N�mero de Vagas"
										size="4" maxlength="5" 	onkeyup="return formatarInteiro(this)" >
									</h:inputText> 
									&nbsp;
									<h:commandButton action="#{inscricaoAtividade.alterarVagas}" 
									image="/img/refresh.png" title="Alterar N�mero de Vagas" style="border: 0; vertical-align: middle;" id="alterarNumVagas" >
								        <f:setPropertyActionListener target="#{inscricaoAtividade.novasVagas}" value="#{atividade.cursoEventoExtensao.numeroVagas}"/>
								        <f:setPropertyActionListener target="#{inscricaoAtividade.idAtividade}" value="#{atividade.id}"/>
									</h:commandButton>	
							</td>
							<td style="width=10%; text-align:right;">???</td>
							<td align="right">${atividade.cursoEventoExtensao.numeroInscritos}</td>
							<td align="right">${atividade.cursoEventoExtensao.numeroVagas - atividade.cursoEventoExtensao.numeroInscritos}</td>
							
							<td width="2%" align="center">
								<h:commandLink title="Gerenciar Inscri��es" action="#{inscricaoAtividade.listarInscricoes}"	id="gerenciarInscricao">
									<f:param name="idAtividade" value="#{atividade.id}" />
									<f:param name="numeroInscritos" value="#{atividade.cursoEventoExtensao.numeroInscritos}" />
									<h:graphicImage url="/img/clipboard.gif" height="20" width="20" />
								</h:commandLink>
							</td>
							
							<%--  
							 OBserva��o: N�o precisa ficar aqui, todas as op��es de inscri��es podem ficar na p�gina de gerenciar inscri��es.
							 
							<td width="2%" align="center">
								<h:commandLink title="Criar Inscri��o" action="#{inscricaoAtividade.preCadastrar}" 	id="criarInscricao">
									<f:param name="idAtividade" value="#{atividade.id}" />
									<f:param name="numeroInscritos" value="#{atividade.cursoEventoExtensao.numeroInscritos}" />
									<h:graphicImage url="/img/adicionar.gif" />
								</h:commandLink>
							</td> 						
							
							
							<td width="2%" align="center">
								<h:commandLink title="Relat�rio de Inscritos" action="#{inscricaoAtividade.exibirRelatorioInscritos}" 
												rendered="#{atividade.cursoEventoExtensao.numeroInscritos > 0}" id="relatorioInscrito">
									<f:param name="idAtividade" value="#{atividade.id}" />
									<h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</td>
							
							<td width="2%" align="center">
								<h:commandLink title="Listar Inscri��es" action="#{inscricaoAtividade.listarInscricoesAtividade}"
											 rendered="#{atividade.cursoEventoExtensao.numeroInscritos > 0}" id="listarInscritos">
									<f:param name="idAtividade" value="#{atividade.id}" />
									<h:graphicImage url="/img/table_go.png"/>
								</h:commandLink>
							</td> 
							
							--%>
							
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<center><span style="color: red">N�o Existem Cursos e Eventos Dispon�veis para Inscri��o</span></center>
			</c:otherwise>
		</c:choose>
		
		
		
		<br/><br/><br/>
		
		
		
		
		
		
		
		
		
		
		<c:set var="subAtividades" value="#{inscricaoAtividade.subAtividadesCoordenador}" />
		<c:choose>
			<c:when test="${not empty subAtividades}">
			
				<table id="tabelaSubAtividades" width="90%" class="listagem">

					<caption>Lista das Mini Atividades para Inscri��o na �rea P�blica </caption>
					<thead>
						<tr>
							<th width="60%">T�tulo da Mini Atividade</th>
							<th style="text-align: right;">N�m. de Vagas</th>
							<th style="text-align: right;">N�m. Inscritos</th>
							<th style="text-align: right;">Vagas Dispon�veis</th>
							<th colspan="1" style="width: 1%;"/>
						</tr>
					</thead>
					<tbody>
						<c:set var="idAtividadeExtensao" value="-1" scope="request"/>
						
						<c:forEach items="#{subAtividades}" var="atividade" varStatus="count">
							
							<c:if test="${ idAtividadeExtensao != atividade.id}">
								<c:set var="idAtividadeExtensao" value="${atividade.id}" scope="request" />
								<tr class="atividadePaiSubAtividade">
									<td colspan="8">${atividade.codigo} - ${atividade.titulo}</td>
								</tr>
							</c:if>
							
							<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td>${atividade.subAtividade.titulo}</td>
								<td width="10%" align="right">
									<h:inputText value="#{atividade.subAtividade.numeroVagas}" immediate="true" id="numVagasMiniAtividade"  label="N�mero de Vagas"
										size="4" maxlength="5" 	onkeyup="return formatarInteiro(this)" >
									</h:inputText> 
									&nbsp;
									<h:commandButton action="#{inscricaoAtividade.alterarVagasMiniAtividade}" 
									image="/img/refresh.png" title="Alterar N�mero de Vagas" style="border: 0; vertical-align: middle;" id="alterarNumVagasMiniAtividade" >
								        <f:setPropertyActionListener target="#{inscricaoAtividade.novasVagasMiniAtividade}" value="#{atividade.subAtividade.numeroVagas}"/>
									    <f:setPropertyActionListener target="#{inscricaoAtividade.idSubAtividade}" value="#{atividade.subAtividade.id}"/>
									</h:commandButton>	
								</td>
								<td align="right">${atividade.subAtividade.numeroInscritos}</td>
								<td align="right">${atividade.subAtividade.numeroVagas - atividade.subAtividade.numeroInscritos}</td>						
								
								<td width="2%" align="center">
									<h:commandLink title="Gerenciar Inscri��es" action="#{inscricaoAtividade.listarInscricoesMiniAtividade}"	id="gerenciarInscricaoMiniAtividade">
										<f:param name="idSubAtividade" value="#{atividade.subAtividade.id}" />
										<f:param name="numeroInscritos" value="#{atividade.subAtividade.numeroInscritos}" />
										<h:graphicImage url="/img/clipboard.gif" height="20" width="20" />
									</h:commandLink>
								</td>
								
								<%--  
							    OBserva��o: N�o precisa ficar aqui, todas as op��es de inscri��es podem ficar na p�gina de gerenciar inscri��es.
								
								<td width="2%" align="center">
									<h:commandLink title="Criar Inscri��o para Mini Atividade" action="#{inscricaoAtividade.preCadastrarInscricaoMiniAtividade}" 	id="criarInscricaoMiniAtividades">
										<f:param name="idSubAtividade" value="#{atividade.subAtividade.id}" />									
										<h:graphicImage url="/img/add2.png" />
									</h:commandLink>
								</td>
								<td width="2%" align="center">
									<h:commandLink title="Relat�rio de Inscritos" action="#{inscricaoAtividade.exibirRelatorioInscritosSubAtividade}" 
											rendered="#{atividade.cursoEventoExtensao.numeroInscritos > 0}" id="relatorioInscritoSubAtividade">
										<f:param name="idSubAtividade" value="#{atividade.subAtividade.id}" />
										<h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</td>
								<td width="2%" align="center">
									<h:commandLink title="Listar Inscri��es" action="#{inscricaoAtividade.listarInscricoesSubAtividade}" 
											rendered="#{atividade.cursoEventoExtensao.numeroInscritos > 0}" id="listarInscritosSubAtividade">
										<f:param name="idSubAtividade" value="#{atividade.subAtividade.id}" />
										<h:graphicImage url="/img/table_go.png"/>
									</h:commandLink>
								</td>
								
								--%>	
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<center><span style="color: red">N�o Existem Mini Atividades Dispon�veis para Inscri��o</span></center>
			</c:otherwise>
		</c:choose>
		
		
		
		<table id="tabelaRodape" style="width: 100%; margin-top: 20px;" class="listagem">
			<tfoot>
				<tr>
					<td align="center" colspan="10">
						<h:commandButton value="<<Voltar" action="#{inscricaoAtividade.cancelar}" /> 
					</td>
				</tr>
			</tfoot>
		</table>
		
		

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>