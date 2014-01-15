<%@include file="/ava/cabecalho.jsp" %>
<f:view>

	<a4j:keepAlive beanName="questionarioTurma" />
	<a4j:keepAlive beanName="categoriaPerguntaQuestionarioTurma" />

	<style>
		.linhaEscura td {
			background:#EEE;
		}
		
		#form:suggestion_docente {
			z-index:99999!important;
		}
		.autocomplete {
			position: static !important;
		}
	</style>

	<%@include file="/ava/menu.jsp" %>
	<%@ taglib uri="/tags/ajax" prefix="ajax"%>
	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<h2><ufrn:subSistema /> &gt; Categorias de perguntas</h2>

	<a4j:region>
		<h:form id="form">
				
			<%-- Botão que exibe o painel de compartilhamento --%>
			<input type="submit" id="bVisualizarCompartilhamento" style="display:none;" onclick="dialogCompartilhamento.show();return false;"/>
			<h:inputHidden value="#{ categoriaPerguntaQuestionarioTurma.idServidor }" id="idServidorSelecionado" />
			
			<%-- Painel que exibe o compartilhamento da categoria selecionada --%>
			<p:dialog header="Compartilhamento da Categoria" widgetVar="dialogCompartilhamento" resizable="false" modal="true" width="530" height="400"  >
				
				<a4j:outputPanel id="basePanelCompartilhamento">
				
					<h:outputText value="<div style='text-align:center;margin:10px;color:#00AA00;font-weight:bold;'>#{ categoriaPerguntaQuestionarioTurma.mensagemSucessoCompartilhamento }</div>" rendered="#{ categoriaPerguntaQuestionarioTurma.mensagemSucessoCompartilhamento != null }" escape="false" />
					<h:outputText value="<div style='text-align:center;margin:10px;color:#AA0000;font-weight:bold;'>#{ categoriaPerguntaQuestionarioTurma.mensagemErroCompartilhamento }</div>" rendered="#{ categoriaPerguntaQuestionarioTurma.mensagemErroCompartilhamento != null }" escape="false" />
				
					<h2><h:outputText value="#{ categoriaPerguntaQuestionarioTurma.categoriaSelecionada.nome }"/></h2>
					
						<table id="formCompartilhar" class="formulario" style="margin-bottom:10px;">
						<caption>Adicionar compartilhamento</caption>
						<tr><td colspan="2"><div class="descricaoOperacao">Para compartilhar esta categoria, digite parte do nome do docente com quem deseja compartilhá-la, selecione o nome correto e clique em "Compartilhar".</div></td></tr>
						<tr><th>Nome:</th><td>
								<h:inputText id="nome" onblur="processObjects();" size="40" style="text-align:left;"/>
								<h:inputHidden id="idServidor" />
								<ajax:autocomplete source="form:nome" target="form:idServidor"
									baseUrl="/sigaa/ajaxServidor" className="autocomplete"
									indicator="indicator" minimumCharacters="3" parameters="tipo=todos,inativos=false"
									parser="new ResponseXmlToHtmlListParser()" />
								<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</td></tr>
						<tfoot><tr><td colspan="2" style="text-align:center;"><a4j:commandButton id="bCompartilhar" oncomplete="limparBusca();" actionListener="#{ categoriaPerguntaQuestionarioTurma.cadastrarCompartilhamento }" reRender="basePanelCompartilhamento" value="Compartilhar" /></td></tr></tfoot>
					</table>
							
					<h:outputText value="<div style='color:#CC0000;font-weight:bold;text-align:center;border:1px solid #CCC;padding:10px;margin:10px;'>Esta categoria ainda não está compartilhada.</div>" escape="false" rendered="#{ fn:length(categoriaPerguntaQuestionarioTurma.categoriaSelecionada.pessoasComCompartilhamento) == 0 }" />
					
					<div style="width:100%;overflow:auto;height:140px;">
						<a4j:outputPanel rendered="#{ fn:length(categoriaPerguntaQuestionarioTurma.categoriaSelecionada.pessoasComCompartilhamento) > 0 }">
							<table style="width:100%;" class="listing">
								<thead><tr><th style="text-align:left;">Nome</th><th style="width:20px;">&nbsp;</th></tr></thead>
								<a4j:repeat var="p" value="#{ categoriaPerguntaQuestionarioTurma.categoriaSelecionada.pessoasComCompartilhamento }" rowKeyVar="indice">
									<tr class="<h:outputText value="#{ indice % 2 == 0 ? 'even' : 'odd' }" />">
										<td><h:outputText value="#{ p.nome }"/></td>
										<td>
											<a4j:commandLink actionListener="#{ categoriaPerguntaQuestionarioTurma.removerCompartilhamento }" onclick="if (!confirm('Tem certeza que deseja finalizar o compartilhamento desta categoria com este usuário?')) return false;" reRender="basePanelCompartilhamento">
												<h:graphicImage value="/img/delete.gif" />
												<f:param name="idPessoa" value="#{ p.id }" />
											</a4j:commandLink>
										</td>
									</tr>
								</a4j:repeat>
							</table>
						</a4j:outputPanel>
					</div>
				</a4j:outputPanel>
			</p:dialog>
		
			<div class="descricaoOperacao">
				<p>Prezado(a) docente,</p>
				
				<p> Essa funcionalidade permite gerenciar todas as questões, arquivadas por categorias, que podem ser utilizadas nos questionários a serem cadastrados. Estas questões também poderão ser reutilizadas para questionários de outras turmas.  </p>
				
				<c:if test="${categoriaPerguntaQuestionarioTurma.size == 0}">
					<p> <b>Para iniciar o gerenciamento do banco de perguntas, cadastre uma categoria para, em seguida, poder cadastrar novas perguntas relacionadas. </b> </p>
				</c:if>
			</div>
	
			<div class="infoAltRem" style="width:80%;">
				<h:graphicImage value="/img/adicionar.gif" /><h:commandLink action="#{categoriaPerguntaQuestionarioTurma.preCadastrar}" value=" Nova Categoria" />
				
				<h:graphicImage value="/img/adicionar.gif" /> 
				<h:commandLink action="#{questionarioTurma.iniciarAdicionarPergunta}" value=" Nova Pergunta">
					<f:param name="adicionar_em_categoria" value="true" />
				</h:commandLink>
	
				<h:graphicImage value="/img/view2.gif" style="overflow: visible;" /> 
				<h:commandLink action="#{categoriaPerguntaQuestionarioTurma.gerarRelatorioTodasPerguntas}" value=" Gerar Relatório de todas Categorias"/>
				
				<br/>			
				<h:graphicImage value="/img/group.png" style="overflow: visible;" />: Compartilhar Categoria
				<h:graphicImage value="/img/view2.gif" style="overflow: visible;" />: Gerar Relatório das Perguntas
				<br/>			
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Categoria
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Categoria<br/>
				
				<h:graphicImage value="/img/baixo.gif" style="overflow: visible;" />: 
				Exibir as Perguntas da Categoria
				
				<h:graphicImage value="/img/cima.gif" style="overflow: visible;" />: 
				Esconder as Perguntas da Categoria
			</div> 
	
			<table class="formulario" width="80%">
				<caption class="listagem">Lista de Categorias (${categoriaPerguntaQuestionarioTurma.size})</caption>
				<thead>
					<tr>
						<th style="width:20px;"></th>
						<th>Nome</th>
						<th width="20"></th>
						<th width="20"></th>
					</tr>
				</thead>
			</table>
				
			<a4j:outputPanel id="categorias">
				<a4j:repeat value="#{categoriaPerguntaQuestionarioTurma.all}" var="c" rowKeyVar="linha">
					
					<div class="<h:outputText value="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" />" style="position:relative;width:80%;margin:auto;border-right:1px solid #CCC;border-left:1px solid #CCC;">
						<span style="display:inline-block;width:20px;" id="exibe_<h:outputText value='#{c.id}' />">
							<a4j:commandLink actionListener="#{categoriaPerguntaQuestionarioTurma.exibirPerguntas}" oncomplete="corrigeIcone(#{c.id});" reRender="categorias">
								<h:graphicImage value="/img/baixo.gif" title="Exibir as perguntas da categoria" rendered="#{empty c.perguntas || !c.exibirPerguntas}" />
								<h:graphicImage value="/img/cima.gif" title="Esconder as perguntas da categoria" rendered="#{not empty c.perguntas && c.exibirPerguntas}" />
								<f:param name="idCategoria" value="#{ c.id }" />
							</a4j:commandLink>
						</span>
						
						<h:outputText value="#{c.nome}" /><h:outputText value=" (#{fn:length(c.perguntas)})" rendered="#{c.exibirPerguntas}" /> 
						
						<div style="float:right;">
							<span style="display:inline-block;width:20px;">
								<a4j:commandLink styleClass="naoImprimir" actionListener="#{ categoriaPerguntaQuestionarioTurma.selecionarCategoria }" reRender="basePanelCompartilhamento" oncomplete="document.getElementById('bVisualizarCompartilhamento').click();esconderAutocomplete();" title="Gerenciar Compartilhamento">
									<h:graphicImage value="/img/group.png" title="Compartilhar Categoria" alt="(Compartilhamento)" />
									<f:param name="idCategoria" value="#{ c.id }" />
								</a4j:commandLink>
							</span>
						
							<span style="display:inline-block;width:20px;">
								<h:commandLink action ="#{categoriaPerguntaQuestionarioTurma.gerarRelatorioPerguntas}" style="border: 0;" title="Gerar Relatório das Perguntas">
									<f:param name="id" value="#{c.id}" />
									<h:graphicImage url="/img/view2.gif" alt="Gerar Relatório das Perguntas" />
								</h:commandLink>
							</span>
							<span style="display:inline-block;width:20px;">
								<h:commandLink action="#{categoriaPerguntaQuestionarioTurma.preAtualizar}" style="border: 0;" title="Alterar Categoria">
									<f:param name="id" value="#{c.id}" />
									<f:param name="voltar_cadastro_pergunta" value="false" />									
									<h:graphicImage url="/img/alterar.gif" alt="Alterar Categoria" />
								</h:commandLink>
							</span>
							<span style="display:inline-block;width:20px;">
								<h:commandLink title="Remover Categoria" style="border: 0;" action="#{categoriaPerguntaQuestionarioTurma.remover}" onclick="#{ confirmDelete }">
									<f:param name="id" value="#{c.id}" />
									<h:graphicImage url="/img/delete.gif" alt="Remover Categoria" />
								</h:commandLink>
							</span>
						</div>
					
						<div style="clear:both;"></div>
						<a4j:outputPanel rendered="#{not empty c.perguntas && c.exibirPerguntas}">
							<div style="padding-bottom:10px;">
								<table class="listagem" style="width:90%;">
									<thead>
										<tr>
											<th style="width:10px;">
												<h:outputText value="<input type='checkbox' onclick='selecionar(this);' />" escape="false" rendered="#{ categoriaPerguntaQuestionarioTurma.voltarAoQuestionario }" />
											</th>
											<th style="width:35px;">Tipo</th>
											<th>Pergunta</th>
											<th style="width:20px;"></th>
											<th style="width:20px;"></th>
										</tr>
									</thead>
								
									<a4j:repeat  value="#{c.perguntas}" var="p" rowKeyVar="linha">
										<tr class="<h:outputText value="#{ linha % 2 == 0 ? '' : 'linhaEscura' }" />" }">
											<td><h:selectBooleanCheckbox value="#{p.selecionada}" rendered="#{ categoriaPerguntaQuestionarioTurma.voltarAoQuestionario}" /></td>
											
											<td><h:graphicImage title="#{ p.tipoString }" value="/ava/img/questionarios/#{ p.tipoIcone }" /></td>
											<td><h:outputText value="#{ p.nome }" /></td>
											<td><h:commandLink id="alterarPergunta" title="Alterar Pergunta" action="#{questionarioTurma.alterarPergunta}">
												<h:graphicImage value="/img/alterar.gif" />
												<f:param name="id" value="#{ p.id }" />
												<f:param name="adicionar_em_categoria" value="true" />
											</h:commandLink></td>
											<td>
												<h:commandLink id="removerPergunta" title="Remover Pergunta" action="#{categoriaPerguntaQuestionarioTurma.removerPergunta}" onclick="#{ confirmDelete }">
													<h:graphicImage value="/img/delete.gif" />
													<f:param name="idPergunta" value="#{ p.id }" />
												</h:commandLink>
											</td>
										</tr>
									</a4j:repeat>
								
								</table>
							</div>
						</a4j:outputPanel>
					</div>
				</a4j:repeat>
				
				<div style="position:relative;width:80%;margin:auto;border-top:1px solid #CCC;"></div>
				
			</a4j:outputPanel>
			
			<table class="formulario" style="margin-top:10px;width:80%;">
				<caption class="listagem">Lista de Categorias Compartilhadas (<h:outputText value="#{ fn:length(categoriaPerguntaQuestionarioTurma.categoriasCompartilhadas)}" />)</caption>
				
				<thead>
					<tr>
						<th style="width:20px;"></th>
						<th style="text-align:left;width:300px;">Nome</th>
						<th style="text-align:left;">Dono</th>
					</tr>
				</thead>
			</table>
				
			<a4j:outputPanel id="categoriasCompartilhadas">
				<a4j:repeat value="#{categoriaPerguntaQuestionarioTurma.categoriasCompartilhadas}" var="c" rowKeyVar="linha">
					
					<div class="<h:outputText value="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" />" style="position:relative;width:80%;margin:auto;border-right:1px solid #CCC;border-left:1px solid #CCC;">
						<span style="display:inline-block;width:20px;" id="exibe_<h:outputText value='#{c.id}' />">
							<a4j:commandLink actionListener="#{categoriaPerguntaQuestionarioTurma.exibirPerguntas}" oncomplete="corrigeIcone(#{c.id});" reRender="categoriasCompartilhadas">
								<h:graphicImage value="/img/baixo.gif" title="Exibir as perguntas da categoria" rendered="#{empty c.perguntas || !c.exibirPerguntas}" />
								<h:graphicImage value="/img/cima.gif" title="Esconder as perguntas da categoria" rendered="#{not empty c.perguntas && c.exibirPerguntas}" />
								<f:param name="idCategoria" value="#{ c.id }" />
								<f:param name="compartilhada" value="true" />
							</a4j:commandLink>
						</span>
						
						<span style='display:inline-block;width:300px;overflow:hidden;'><h:outputText value="#{c.nome}" /> <h:outputText value=" (#{fn:length(c.perguntas)})" rendered="#{c.exibirPerguntas}" /></span><h:outputText value="#{c.dono.pessoa.nome}" /> 
						
						<div style="float:right;">
							<span style="display:inline-block;width:20px;">
								<h:commandLink action ="#{categoriaPerguntaQuestionarioTurma.gerarRelatorioPerguntas}" style="border: 0;" title="Gerar Relatório das Perguntas">
									<f:param name="id" value="#{c.id}" />
									<h:graphicImage url="/img/view2.gif" alt="Gerar Relatório das Perguntas" />
								</h:commandLink>
							</span>
						</div>
					
						<div style="clear:both;"></div>
						<a4j:outputPanel rendered="#{not empty c.perguntas && c.exibirPerguntas}">
							<div style="padding-bottom:10px;">
								<table class="listagem" style="width:90%;">
									<thead>
										<tr>
											<th style="width:10px;">
												<h:outputText value="<input type='checkbox' onclick='selecionar(this);' />" escape="false" rendered="#{ categoriaPerguntaQuestionarioTurma.voltarAoQuestionario }" />
											</th>
											<th style="width:35px;">Tipo</th>
											<th>Pergunta</th>
											<th style="width:20px;"></th>
											<th style="width:20px;"></th>
										</tr>
									</thead>
								
									<a4j:repeat  value="#{c.perguntas}" var="p" rowKeyVar="linha">
										<tr class="<h:outputText value="#{ linha % 2 == 0 ? '' : 'linhaEscura' }" />" }">
											<td><h:selectBooleanCheckbox value="#{p.selecionada}" rendered="#{ categoriaPerguntaQuestionarioTurma.voltarAoQuestionario}" /></td>
											
											<td><h:graphicImage title="#{ p.tipoString }" value="/ava/img/questionarios/#{ p.tipoIcone }" /></td>
											<td><h:outputText value="#{ p.nome }" /></td>
											<td><h:commandLink id="alterarPerguntaCompartilhada" title="Alterar Pergunta" action="#{questionarioTurma.alterarPergunta}">
												<h:graphicImage value="/img/alterar.gif" />
												<f:param name="id" value="#{ p.id }" />
												<f:param name="adicionar_em_categoria" value="true" />
											</h:commandLink></td>
											<td>
												<h:commandLink id="removerPerguntaCompartilhada" title="Remover Pergunta" action="#{categoriaPerguntaQuestionarioTurma.removerPergunta}" onclick="if (!confirm('Tem certeza que deseja remover esta pergunta compartilhada? Ela será apagada para todos os usuários do compartilhamento')) return false;">
													<h:graphicImage value="/img/delete.gif" />
													<f:param name="idPergunta" value="#{ p.id }" />
												</h:commandLink>
											</td>
										</tr>
									</a4j:repeat>
								
								</table>
							</div>
						</a4j:outputPanel>
					</div>
				</a4j:repeat>
				
				<div style="position:relative;width:80%;margin:auto;border-top:1px solid #CCC;"></div>
				
			</a4j:outputPanel>
				
			<c:if test="${ categoriaPerguntaQuestionarioTurma.voltarAoQuestionario}">
			
				<table class="formulario" style="width:80%;"><tfoot><tr><td>
					<h:commandButton value="Adicionar ao questionário" action="#{ questionarioTurma.adicionarPerguntasDoBanco }" />
				
					<h:commandButton value="<< Voltar ao Questionário" action="#{questionarioTurma.gerenciarPerguntasDoQuestionarioSemSalvar}" />
				</td></tr></tfoot></table>
				
			</c:if>
		</h:form>
	</a4j:region>
</f:view>

<script>

function corrigeIcone (id) {
	var span = document.getElementById("exibe_"+id);
	var imagem = span.firstChild.firstChild;
	var exibindo = span.nextSibling.nextSibling.nextSibling.nextSibling != null;
	
	imagem.src = "${ctx}/img/" + (exibindo ? "cima.gif" : "baixo.gif");
}

function selecionar (check){
	var checado = check.checked;
	var tabela = jQuery(check.parentNode.parentNode.parentNode.parentNode);
	jQuery('input', tabela).each(function () { jQuery(this).attr('checked', checado); });
}

function esconderAutocomplete () {
	var autocomplete = document.getElementById("ajaxAuto_form:nome");
	var parent = autocomplete.parentNode;
	parent.removeChild(autocomplete);
}

function processObjects() {
	var servidor = document.getElementById('form:idServidor');
	var servidorSelecionado = document.getElementById('form:idServidorSelecionado');
	servidorSelecionado.value = servidor.value;
}

function limparBusca () {
	document.getElementById('form:nomeDocente').value = "";
	document.getElementById('form:idServidorSelecionado').value = "";
	document.getElementById('form:bCompartilhar').style.display = "none";
}

</script>

<%@include file="/ava/rodape.jsp" %>