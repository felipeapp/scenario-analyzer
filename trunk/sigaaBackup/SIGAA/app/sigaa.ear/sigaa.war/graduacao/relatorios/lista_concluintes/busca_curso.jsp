<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<style>
.rich-progress-bar-width { width: 800px;}
.rich-progress-bar-uploaded-dig {font-size: 16px;}
.rich-progress-bar-shell-dig {font-size: 16px;}
</style> 

<f:view>
	<h2><ufrn:subSistema /> > Lista de Assinaturas para Colação de Grau</h2>
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Antes de gerar a lista de assinaturas para a colação de grau, os
			discentes terão seus históricos recalculados. Por favor, aguarde até o
			término da operação.</p>
	</div>
	<h:form id="form">
	<center>
	<a4j:keepAlive beanName="listaAssinaturasGraduandos"></a4j:keepAlive>
	<a4j:outputPanel id="progressPanel">
		<rich:progressBar id="progressBar" minValue="0" maxValue="100"
			value="#{ listaAssinaturasGraduandos.percentualProcessado }"
			label="#{ listaAssinaturasGraduandos.mensagemProgresso }"
			reRenderAfterComplete="form">
			<f:facet name="initial">
				<h:panelGroup>
						<table class="formulario" width="85%">
							<caption>Selecione o Curso e Ano-Período dos Graduandos</caption>
							<tr>
								<th width="15%" class="required">Curso:</th>
								<td width="70%" style="text-align: left;">
									<a4j:region>
										<h:selectOneMenu id="curso" value="#{listaAssinaturasGraduandos.obj.id}" rendered="#{listaAssinaturasGraduandos.inicial}">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
											<f:selectItems value="#{listaAssinaturasGraduandos.cursoCombo}" />
											<a4j:support event="onchange" action="#{listaAssinaturasGraduandos.cursoListener}" />
										</h:selectOneMenu>
									</a4j:region>
								</td>
							</tr>
							<tr>
								<th class="required"><h:outputText value="Pólo:" rendered="#{listaAssinaturasGraduandos.inicial && listaAssinaturasGraduandos.obj.ADistancia}" /></th>
								<td style="text-align: left;">
									<h:selectOneMenu id="polo" value="#{listaAssinaturasGraduandos.polo.id}" rendered="#{listaAssinaturasGraduandos.inicial && listaAssinaturasGraduandos.obj.ADistancia}">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{listaAssinaturasGraduandos.poloCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th class="required">Ano-Período:</th>
								<td style="text-align: left;">
									<h:inputText value="#{listaAssinaturasGraduandos.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" id="ano" />
									- <h:inputText value="#{listaAssinaturasGraduandos.periodo}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" id="periodo" />
								</td>
							</tr>
							<tfoot>
								<tr>
									<td colspan="2" align="center">
										<a4j:commandButton action="#{listaAssinaturasGraduandos.buscarGraduandos}" value="Buscar Graduandos deste Curso" 
											id="buscarGraduandos" reRender="progressPanel" disabled="#{ listaAssinaturasGraduandos.disable }"  onclick="this.value='Por favor, aguarde...'"/>
										<h:commandButton action="#{listaAssinaturasGraduandos.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
									</td>
								</tr>
							</tfoot>
						</table>
						<br>
						<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
				</h:panelGroup>
			</f:facet>
			<f:facet name="complete">
				<h:panelGroup>
		        	<table class="formulario" width="90%">
							<caption>Lista de Assinaturas para Colação de Grau</caption>
							<tr>
								<th width="15%" class="rotulo">Curso:</th>
								<td width="70%" style="text-align: left;">${listaAssinaturasGraduandos.obj.descricaoCompleta}</td>
							</tr>
							<tr>
								<th class="rotulo"><h:outputText value="Pólo:" rendered="#{listaAssinaturasGraduandos.obj.ADistancia}" /></th>
								<td style="text-align: left;">
									<h:outputText value="#{listaAssinaturasGraduandos.polo.descricao}"/>
								</td>
							</tr>
							<tr>
								<th class="rotulo">Ano-Período:</th>
								<td style="text-align: left;">${listaAssinaturasGraduandos.ano}.${listaAssinaturasGraduandos.periodo}</td>
							</tr>
							<tr>
								<td colspan="2" class="subFormulario">Discentes Aptos a Colar Grau (${ fn:length(listaAssinaturasGraduandos.graduandos) })</th>
							</tr>
							<tr>
								<td colspan="2">
									<c:if test="${ fn:length(listaAssinaturasGraduandos.graduandos) > 0}">
									<table class="listagem">
									<thead>
										<tr><th style="text-align: right;" width="12%">Matrícula</th><th>Nome</th></tr>
									</thead>
									<c:forEach items="#{ listaAssinaturasGraduandos.graduandos }" var="item"varStatus="status">
										<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
											<td style="text-align: right;">${ item.matricula }</td>
											<td>${ item.nome }</td>
										</tr>
									</c:forEach>
									</table>
									</c:if>
									<c:if test="${ fn:length(listaAssinaturasGraduandos.graduandos) == 0}">
										Não há discentes aptos a colar grau no momento.
									</c:if>
								</td>
							</tr>
							<c:if test="${ not empty listaAssinaturasGraduandos.inaptos }">
								<tr>
									<td colspan="2" class="subFormulario">Discentes Inaptos a Colar Grau Coletivo no Ano-Período (${ fn:length(listaAssinaturasGraduandos.inaptos) })</th>
								</tr>
								<tr>
									<td colspan="2">
										<table class="listagem">
										<thead>
											<tr><th style="text-align: right;" width="12%">Matrícula</th>
											<th width="30%">Nome</th>
											<th>Motivo</th></tr>
										</thead>
										<c:forEach items="#{ listaAssinaturasGraduandos.inaptos }" var="item"varStatus="status">
											<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
												<td style="text-align: right;">${ item.key.matricula }</td>
												<td>${ item.key.nome }</td>
												<td>${ item.value }</td>
											</tr>
										</c:forEach>
										</table>
									</td>
								</tr>
							</c:if>
							<tfoot>
								<tr>
									<td colspan="2" align="center">
										<h:commandButton action="#{listaAssinaturasGraduandos.formListaAssinatura}" value="Gerar Lista de Assinaturas" id="gerarLista" 
											rendered="#{ not empty listaAssinaturasGraduandos.graduandos }"/>
										<h:commandButton action="#{listaAssinaturasGraduandos.iniciar}" value="Nova Busca" id="reiniciar" />
										<h:commandButton action="#{listaAssinaturasGraduandos.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar2"/>
									</td>
								</tr>
							</tfoot>
						</table>
	        	</h:panelGroup>
	        </f:facet>
		</rich:progressBar>
	</a4j:outputPanel>
	</center>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

