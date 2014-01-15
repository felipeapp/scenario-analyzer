
<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

<a4j:keepAlive beanName="inscricaoParticipanteAtividadeMBean" />

<c:if test="${inscricaoParticipanteAtividadeMBean.qtdPeriodosInscricaoAbertos == 0}">
	${inscricaoParticipanteAtividadeMBean.carregaAtividadesPeriodosInscricaoAbertos}
</c:if>

<h2>Lista de Cursos e Eventos de Extensão com Períodos de Inscrição Abertos</h2>
	
	<h:form id="formListaCursosEventosExtensao">    

		<table class="formulario" style="width: 60%; margin-bottom: 20px;">
			<caption>Busca por Ações de Extensão</caption>

			<tbody>
				<tr>
	                <td width="3%" align="center">
	                    <h:selectBooleanCheckbox value="#{inscricaoParticipanteAtividadeMBean.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder" /> 
	                </td>
	                <td width="17%">
	                    <label for="formListaCursosEventosExtensao:selectBuscaTitulo">Título da Ação:</label>
	                </td>
	                <td><h:inputText id="buscaTitulo" value="#{inscricaoParticipanteAtividadeMBean.tituloAtividade}" size="50" 
	                            onchange="javascript:$('formListaCursosEventosExtensao:selectBuscaTitulo').checked = true;" /></td>
	           </tr>
	
	            <tr>
	                <td align="center">
	                    <h:selectBooleanCheckbox value="#{inscricaoParticipanteAtividadeMBean.checkBuscaTipoAtividade}" 
	                            id="selectBuscaTipoAtividade" styleClass="noborder" /> 
	                    </td>
	                <td><label for="formListaCursosEventosExtensao:selectBuscaTipoAtividade">Tipo de Atividade:</label></td>
	                <td>            
	                    <h:selectOneMenu id="buscaTipoAcao" value="#{inscricaoParticipanteAtividadeMBean.idTipoAtividade}" 
	                            onchange="javascript:$('formListaCursosEventosExtensao:selectBuscaTipoAtividade').checked = true;">
	                        <f:selectItem itemLabel="TODOS" itemValue="0" />
	                        <f:selectItems value="#{tipoAtividadeExtensao.cursoEventoCombo}" />
	                    </h:selectOneMenu>           
	                </td>
	            </tr>
	
	            <tr>    
	                <td align="center"> 
	                    <h:selectBooleanCheckbox value="#{inscricaoParticipanteAtividadeMBean.checkBuscaAreaTematica}" id="selectBuscaArea" styleClass="noborder" /> 
	                </td>
	                <td><label for="formListaCursosEventosExtensao:selectBuscaArea">Área Temática:</label></td>
	                <td>
	                    <h:selectOneMenu id="buscaArea" value="#{inscricaoParticipanteAtividadeMBean.idAreaTematica}" 
	                            onchange="javascript:$('formListaCursosEventosExtensao:selectBuscaArea').checked = true;">
	                        <f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
	                        <f:selectItems value="#{areaTematica.allCombo}" />
	                    </h:selectOneMenu>
	                 </td>
	            </tr>
	        
	            <tr>
	                <td align="center"> 
	                    <h:selectBooleanCheckbox value="#{inscricaoParticipanteAtividadeMBean.checkBuscaCoordenador}" 
	                            id="selectBuscaServidor" styleClass="noborder" />  
	                </td>
	                <td><label for="formListaCursosEventosExtensao:selectBuscaServidor">Coordenador:</label></td>
	                <td>
	                    <h:inputHidden id="buscaServidor" value="#{inscricaoParticipanteAtividadeMBean.coordenador.id}" />
	                    <h:inputText id="buscaNome" value="#{inscricaoParticipanteAtividadeMBean.coordenador.pessoa.nome}" 
	                            size="50" onchange="javascript:$('formListaCursosEventosExtensao:selectBuscaServidor').checked = true;" /> 
	                            
	                    <ajax:autocomplete source="formListaCursosEventosExtensao:buscaNome" target="formListaCursosEventosExtensao:buscaServidor" minimumCharacters="3"
	                            baseUrl="/sigaa/ajaxServidor" className="autocomplete" indicator="indicator"  
	                            parameters="tipo=todos,inativos=false,areaPublica=true" parser="new ResponseXmlToHtmlListParser()" /> 
	                    <span id="indicator" style="display:none;"> 
	                        <img src="/sigaa/img/indicator.gif" /> 
	                    </span>
	                </td>
	            </tr>
	            
	            <tr>
	                <td align="center"> 
	                    <h:selectBooleanCheckbox value="#{inscricaoParticipanteAtividadeMBean.checkBuscaPeriodo}" id="selectBuscaPeriodo" styleClass="noborder" /> 
	                </td>
	                <td><label for="formListaCursosEventosExtensao:selectBuscaPeriodo">Período:</label></td>
	                <td><t:inputCalendar id="periodoInicio" value="#{inscricaoParticipanteAtividadeMBean.dataInicioPeriodoInscricao}" renderAsPopup="true" 
	                            renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
	                            popupTodayString="Hoje é" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
	                            onchange="javascript:$('formListaCursosEventosExtensao:selectBuscaPeriodo').checked = true;">
	                        <f:converter converterId="convertData" />
	                    </t:inputCalendar> 
	                    <i>até</i> 
	                    <t:inputCalendar id="periodoFim" value="#{inscricaoParticipanteAtividadeMBean.dataFimPeriodoInscicao}" renderAsPopup="true" 
	                            renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
	                            popupTodayString="Hoje é" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
	                            onchange="javascript:$('formListaCursosEventosExtensao:selectBuscaPeriodo').checked = true;">
	                        <f:converter converterId="convertData" />
	                    </t:inputCalendar></td>
	            </tr> 
	        </tbody>
	        
	        <tfoot>
	            <tr>
	                <td colspan="3">
	                    <h:commandButton actionListener="#{inscricaoParticipanteAtividadeMBean.buscaPeriodosInscricoesAbertos}" value="Filtrar" id="cmdFiltarPeriodosInscricao" />
	                </td>
	            </tr>
	        </tfoot>

		</table>

		<c:choose>
			<c:when test="${inscricaoParticipanteAtividadeMBean.qtdPeriodosInscricaoAbertos == 0}">
				<center style="color:red;"><i>Nenhum Curso ou Evento de Extensão com período de inscrição aberto </i></center>
			</c:when>
	
			<c:otherwise>
				
					<div class="infoAltRem">
						<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Ver Detalhes do Curso ou Evento de Extensão.
		    			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Inscrever-se
					</div>
					<table class="listagem"  style="width:100%;">
						<caption class="listagem">Inscrições Abertas ( ${inscricaoParticipanteAtividadeMBean.qtdPeriodosInscricaoAbertos} ) </caption>
						<thead>
							<tr>
								<th rowspan="2" style="width: 50%;">Título</th>
								<th rowspan="2" width="10%">Tipo</th>
								<th rowspan="2" style="text-align:center">Inscrições até</th>
								<th colspan="4" style="text-align:center;">Vagas</th>
								<th rowspan="2" width="1%" />
								<th rowspan="2" width="1%" />
							</tr>
							
							<tr>
								
								<th style="text-align:right">Total</th>
								<th style="text-align:right">Aprovadas</th>
								<th style="text-align:right">Pendentes</th>
								<th style="text-align:right">Restantes</th>
								
							</tr>
							
						</thead>
						<tbody>
							<c:forEach items="#{inscricaoParticipanteAtividadeMBean.periodosInscricoesAbertos}" var="periodoAberto" varStatus="status">
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
									
									<td>
										<span style="font-weight: bold;">${periodoAberto.atividade.titulo}</span> <br/>
										<font style="font-size: x-small;"> <i>Coordenação: ${periodoAberto.atividade.coordenacao.pessoa.nome}</i> </font>								
									</td>
									
									<td>${periodoAberto.atividade.tipoAtividadeExtensao.descricao}</td>
									<td style="text-align:center">
										<h:outputText value="#{periodoAberto.periodoFim}">
											<f:convertDateTime pattern="dd/MM/yyyy" />
										</h:outputText>
									</td>
									
									<td style="text-align:right">${periodoAberto.quantidadeVagas}</td>
				                    <td style="text-align:right">${periodoAberto.quantidadeInscritosAprovados}</td>
				                    <td style="text-align:right">${periodoAberto.quantidadeInscritos}</td>
									<td style="text-align:right">${periodoAberto.quantidadeVagasRestantes}</td>
									
									
									<td align="center">
										<h:commandLink title="Ver Detalhes do Curso ou Evento de Extensão. " action="#{inscricaoParticipanteAtividadeMBean.visualizarDadosCursoEvento}">
											<f:param name="idAtividadeExtensaoSelecionada" value="#{periodoAberto.atividade.id}" />
											<h:graphicImage url="/img/view.gif" />
										</h:commandLink>
									</td>
									
									<td align="center">
									
										<%-- Redireciona via pretty faces, mesmo operação se o usuário clica no link 
										"Acesso à Área de Inscritos em Cursos e Eventos" diretamente no menu de extensão 
										--%>
										<h:outputLink title="Inscrever-se" rendered="#{periodoAberto.aberta}" value="/sigaa/link/public/extensao/acessarAreaInscrito">
											<h:graphicImage url="/img/seta.gif" />
										</h:outputLink>
										
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				
			</c:otherwise>
			
		</c:choose>
		
	</h:form>
	
	
	<br />
	<br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; voltar ao menu principal</a>
	</div>
	<br />
	
</f:view>

<%@include file="/public/include/rodape.jsp" %>
	