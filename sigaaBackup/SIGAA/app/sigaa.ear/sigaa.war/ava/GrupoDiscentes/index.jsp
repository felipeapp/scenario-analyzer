<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<a4j:keepAlive beanName="grupoDiscentes" />
	<%@include file="/ava/menu.jsp" %>
	<h:form id="formGrupos">
	
	<fieldset>
		<div id="auxOffset"></div>
		<legend>Gerenciar Grupos de Discentes</legend>
				
		<c:if test="${not empty grupoDiscentes.discentes || grupoDiscentes.numeroGrupos > 0}">	
			
			<div class="descricaoOperacao">
				<p>Nesta tela é possível agrupar os alunos para informar ao sistema como eles estão divididos para realizar tarefas na turma.</p>
				<p>Inicialmente, defina no quadro <b>Configurar Grupos</b> a quantidade de grupos que a turma será dividida.</p> 
				<p>Após isso, mova os alunos para seus grupos.</p>
				<p>Para finalizar as modificações nos grupos, clique em <b>Salvar</b>, no final desta tela.</p>
				<p>É possível deslizar os grupos para cima ou para baixo, clicando nas setas.</p>
				<p>É possível alterar o nome dos grupos clicando no ícone <img src="/sigaa/ava/img/group_edit.png" style="vertical-align:middle;">.</p>
				<p>Também é possível permitir que os alunos alterem os nomes dos seus próprios grupos. Para isso clique <h:commandLink value="aqui" action="#{grupoDiscentes.iniciarConfiguracoesAva}"/>, 
				e marque "Sim" na opção "Alunos podem alterar o nome dos grupos?".</p>
			</div>
			
			<div class="infoAltRem">
				<img src="/sigaa/ava/img/group_edit.png">: Alterar Nome do Grupo
			</div>
			
			<style>
				.dropTargetPanel {
					margin-bottom:10px;
					margin-left:10px;
					
				}
				
				.top td{
					vertical-align:top;
				}
				
				.coluna {
					width:33.33%;
				}
				
				.cEsquerda {
					width:50%;
					padding-right:5px;
					vertical-align:top;
					padding-bottom:5px;
				}
				
				.cDireita {
					vertical-align:top;
				}
				
				.semGrupo {
					width:33.33%;
					vertical-align:top;
				}
				
				.grupos {
					vertical-align:top;
					padding-left:5px;
				}
				
				.discente {
					border:1px solid #99BBE8;
					padding:0px;
					margin-bottom:5px;
				}
				
				.discenteBody {
					position:relative;
					padding:0px;
					overflow:hidden;
					height:30px;
					cursor: move;
				}
				
				.discenteHandle {
					position:absolute;
					left:0px;
					width:15px;
					cursor:move;
					background:url("/sigaa/ava/img/handle.jpg") no-repeat #CCCCCC;
					text-align:center;
					height:30px;
					cursor: move;
				}
				
				.discenteTexto {
					margin-left:25px;
					cursor: move;
				}
				
				.formGrupos {
					padding:0px !important;
				}
				
				.discenteTrancado {
					color:#FF0000;
				}
			
			</style>
			<a4j:commandButton id="atualizarListagens" actionListener="#{grupoDiscentes.moverDiscente}" style="display:none;" reRender="alunosSemGrupo,panelGrupos" />
			<script>
				function atualizaGrupos () {
					document.getElementById("formGrupos:atualizarListagens").click();
				}
			</script>
	
			<rich:dragIndicator id="indicator" />

			<h:panelGrid columns="2" columnClasses="semGrupo,grupos" style="width:100%;vertical-align:top;" >
				<a4j:outputPanel id="semGrupos" style="display:block;">
					<div id="ajustador1" style="padding-bottom:0px"></div>
					<div  style="margin-left:50%;padding-bottom:5px;">
						<h:graphicImage onclick="moveUp(event,true)" value="/ava/img/painel_seta_cima.png" title="Deslizar para Cima" alt="Deslizar para Cima" />
					</div>
					<rich:panel header="Alunos sem grupo (#{ fn:length(grupoDiscentes.discentes) })" styleClass="ui-corner-all" id="alunosSemGrupo" headerClass="headerBloco ui-corner-all" style="vertical-align:top;">
						<a4j:repeat value="#{grupoDiscentes.discentes}" var="d">
							<rich:panel id="discente" styleClass="discente ui-corner-all" bodyClass="discenteBody" style="cursor:move;margin-bottom:10px;">
								<a4j:outputPanel layout="block" styleClass="discenteHandle" />
								
								<rich:dragSupport dragIndicator="indicator" dragType="discente" dragValue="#{d.id}">
									<rich:dndParam type="drag" name="label" value="#{d.matricula} - #{d.nome}"/>
								</rich:dragSupport>
								
								<a4j:outputPanel layout="block" styleClass="discenteTexto">
									<h:outputText value="#{d.matricula} - #{d.pessoa.nome}" styleClass="#{d.selecionado ? 'discenteTrancado' : ''}" />
									
								</a4j:outputPanel>
							</rich:panel>
						</a4j:repeat>				
						<rich:dropSupport acceptedTypes="discente" dropValue="semGrupo" dropListener="#{grupoDiscentes.moverDiscente}" reRender="alunosSemGrupo, panelGrupos" />	
					</rich:panel>
					<div  style="margin-left:50%;padding-top:5px;"">
						<h:graphicImage onclick="moveDown(event,true)" value="/ava/img/painel_seta_baixo.png" title="Deslizar para Baixo" alt="Deslizar para Baixo" />
					</div>
			</a4j:outputPanel>			
				<h:panelGroup id="comGrupos" style="display:block;vertical-align:top;">
					<rich:panel id="panelConfig" style="width:100%;margin-bottom:5px;vertical-align:top;" bodyClass="formGrupos" styleClass="ui-corner-all" headerClass="headerBloco ui-corner-all">
						<f:facet name="header"><h:outputText value="Configurar Grupos" /></f:facet>
						<table class="formulario" style="width:100%;border:none;">
							<tr>
								<th><h:outputText value="Quantos grupos a turma terá?" /><span class="required"></span></th>
								<td><h:inputText value="#{grupoDiscentes.numeroGrupos}" onkeyup="return formatarInteiro(this);" maxlength="2" /></td>
							</tr>
							<tr>
								<th><h:selectBooleanCheckbox id="aleatorio" value="#{grupoDiscentes.gerarGruposAleatorios}" /></th>
								<td><label for="formGrupos:aleatorio">Alocar os alunos sem grupos aleatoriamente</label><br /></td>
							</tr>
							<tfoot>
								<tr style="background:none;"><td colspan="2" style="background:#CDDEF3; -moz-border-radius: 0px 0px 4px 4px;"><a4j:commandButton actionListener="#{grupoDiscentes.gerenciarNumeroGrupos}" value="Atualizar" reRender="semGrupos,panelGrupos" /><br /></td></tr>
							</tfoot>
						</table>
					</rich:panel>
					<div id="ajustador2" style="padding-bottom:0px"></div>
					<a4j:outputPanel id="panelGrupos">
						<c:if test="${not empty grupoDiscentes.grupos}">
							<div  style="margin-left:50%;padding-bottom:5px;">
								<h:graphicImage onclick="moveUp(event,false)" value="/ava/img/painel_seta_cima.png" title="Deslizar para Cima" alt="Deslizar para Cima" />
							</div>
						</c:if>
						<h:panelGrid columns="1" columnClasses="cEsquerda,cDireita" rowClasses="alignTop" style="width:100%;vertical-align:top;">
							<c:forEach items="#{grupoDiscentes.grupos}" var="g">
								<rich:panel id="grupo" styleClass="grupo ui-corner-all" headerClass="headerBloco ui-corner-all">
									<f:facet name="header">
										<a4j:outputPanel>
											<h:outputText value="#{g.nome} (#{ fn:length(g.discentes) })" />
											<a4j:commandLink id="atualizarNome" title="Alterar Nome do Grupo" actionListener="#{grupoDiscentes.atualizar}" oncomplete="document.getElementById('bVisualizarInformacoesGrupo').onclick();" reRender="nomePanel" >
												<h:graphicImage value="/ava/img/group_edit.png" style="vertical-align:middle;margin-left:5px;"/>
												<f:param name="idGrupo" value="#{g.id}" />
												<f:param name="numCriacao" value="#{g.numCriacao}" />
											</a4j:commandLink>	
										</a4j:outputPanel>	
									</f:facet>
								
									<a4j:repeat value="#{g.discentes}" var="d">
										<rich:panel id="discente" styleClass="discente ui-corner-all" bodyClass="discenteBody" style="cursor:move;margin-bottom:10px;">
											<a4j:outputPanel layout="block" styleClass="discenteHandle" />
											
											<rich:dragSupport dragIndicator="indicator" dragType="discente" dragValue="#{d.id}">
												<rich:dndParam type="drag" name="label" value="#{d.matricula} - #{d.nome}" />
											</rich:dragSupport> 
											
											<a4j:outputPanel layout="block" styleClass="discenteTexto">
												<h:outputText value="#{d.matricula} - #{d.pessoa.nome}" styleClass="#{d.selecionado ? 'discenteTrancado' : ''}" />
											</a4j:outputPanel>
										</rich:panel>
									</a4j:repeat>
									
									<rich:dropSupport acceptedTypes="discente" dropValue="#{g.nome}" dropListener="#{grupoDiscentes.moverDiscente}" reRender="alunosSemGrupo,panelGrupos" />
								</rich:panel>
							</c:forEach>
						</h:panelGrid>
						<a4j:outputPanel layout="block" styleClass="clear" />
						<c:if test="${not empty grupoDiscentes.grupos}">
							<div  style="margin-left:50%;">
								<h:graphicImage onclick="moveDown(event,false)" value="/ava/img/painel_seta_baixo.png" title="Deslizar para Baixo" alt="Deslizar para Baixo" />
							</div>
						</c:if>
					</a4j:outputPanel>
				</h:panelGroup>
			</h:panelGrid>
		
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{grupoDiscentes.salvar}" value="Salvar" />
					<h:commandButton onclick="return confirm('Deseja realmente desfazer os grupos?')" action="#{grupoDiscentes.desfazer}" value="Desfazer" />  
				</div>
				<div class="other-actions">
					<h:commandButton action="#{ grupoDiscentes.cancelar }" onclick="#{confirm}"  value="Cancelar"/> 
				</div>
				<div class="required-items">
					<span class="required">&nbsp;</span>
					Campos de Preenchimento Obrigatório
				</div>
			</div>

			</c:if>
			<c:if test="${empty grupoDiscentes.discentes && grupoDiscentes.numeroGrupos == 0}">	
				<div style="line-height:200px;text-align:center;font-size:1.3em;font-weight:bold;color: #FF0000;">Não existem alunos matriculados nesta turma.</div>
			</c:if>
		</fieldset>
		<h:inputHidden value="#{ grupoDiscentes.nomeGrupo }" id="idNomeGrupo" />
		<input type="submit" id="bVisualizarInformacoesGrupo" style="display:none;" onclick="dialogInfoGrupo.show();return false;"/>
		<%-- Painel das informações sobre os exemplares. --%>
		<p:dialog id="panelGrupo" styleClass="panelPerfil" header="ALTERAR NOME DO GRUPO " widgetVar="dialogInfoGrupo" modal="true" resizable="false" width="500" height="130">
				<br/>
				<ul class="form">
					<li>
						<label><h:outputText value="Nome" />:</label>
						<a4j:outputPanel id="nomePanel">
						<h:inputText id="nome" onblur="processObjects();" value="#{grupoDiscentes.nomeGrupo}"  size="50" maxlength="55"/>
						</a4j:outputPanel>
					</li>
				</ul>
				<br/>
				<div align="center">
					<a4j:commandButton  value="Alterar" actionListener="#{grupoDiscentes.alterar}" onclick="dialogInfoGrupo.hide();"  reRender="panelGrupos"/> 
				</div>
		</p:dialog>
			
	</h:form>
</f:view>

<script type="text/javascript">
var pixel = 0;
countDownDir = 0;

function moveDown(event,discentes) {

	var element = null;
	var alturaSpanEsquerda = document.getElementById("formGrupos:semGrupos").clientHeight;
	var alturaSpanDireita = document.getElementById("formGrupos:comGrupos").clientHeight;
	var descer = false;
	var height = null;
	var downDir = false;
	
	if (discentes){
		element = jQuery("#ajustador1");
		if (alturaSpanEsquerda < alturaSpanDireita)
			descer = true;
		if ( alturaSpanDireita - alturaSpanEsquerda < 300 )
			height = alturaSpanDireita - alturaSpanEsquerda;
	}		
	else {	
		element = jQuery("#ajustador2");
		if (alturaSpanDireita < alturaSpanEsquerda)
			descer = true;
		if ( alturaSpanEsquerda - alturaSpanDireita < 300 )
			height = alturaSpanEsquerda - alturaSpanDireita;
		downDir = true;
	}
	
	if ( descer ) {
		if ( height == null ){
			element.animate({height:'+=300'},1200); 
			pixel += 300;
		} else {
			heightString = "+=" + height 
			element.animate({height:heightString},1200); 
			pixel += height;
		}
		if (downDir)
			countDownDir++;
	}	
}

function moveUp(event,discentes) {

	if ( pixel != 0 ) {
		var element = null;
		if (discentes)	
			element = jQuery("#ajustador1");
		else {	
			element = jQuery("#ajustador2");
			if ( countDownDir == 0 )
				return;
		}
		if ( pixel >= 300 ){
			element.animate({marginBottom:'-=300'},1200); 
			pixel -= 300;
		} else {
			pixelString = "-=" + pixel;
			element.animate({marginBottom:pixelString},1200); 
			pixel = 0;
		}		
		if (!discentes)
			countDownDir--;
	}
}

function processObjects() {
	var nome = document.getElementById('formGrupos:nome');
	var nomeSelecionado = document.getElementById('formGrupos:idNomeGrupo');
	nomeSelecionado.value = nome.value;
}
</script>

<%@include file="/ava/rodape.jsp" %>