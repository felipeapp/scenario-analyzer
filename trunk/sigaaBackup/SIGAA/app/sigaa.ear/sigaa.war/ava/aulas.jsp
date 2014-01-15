<script type="text/javascript" src="${ctx}/avaliacao/flowplayer/flowplayer-3.2.2.min.js"></script>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>

<%-- <script>var maximoSlider = ${fn:length(topicoAula.comboIdentado) };</script> --%>
<script src='/sigaa/ava/javascript/aulas.js'></script>

<script type="text/javascript">
function lancarAcaoTopico(elem) {
	if (elem.value.split('_')[1] > 0) {
		if(elem.value.split('_')[1] == 7 && !confirm('Deseja realmente remover o registro de aula?')) {
			elem.selectedIndex = 0; //Se o usuário cancelar a remoção, setar o valor selecionado como o primeiro da lista.
			return null;			
		}
		
		acaoTopico(elem);
	}
}
</script>

<style type="text/css">
	.acao-topico {
		height:20px;
	}

	.acao-topico .baseAcaoTopico {
		position:relative;
		float:right;
		width:100px;
	}
	
	.acao-topico .baseAcaoTopico span span .yui-module, #auxAcaoTopico .baseAcaoTopico span span .yui-module {
		width:180px;
		left:-90px !important;
		z-index:999!important;
		text-align:left;
	}
	
	#auxAcaoTopico .baseAcaoTopico span span .yui-module {
		left:-110px !important;
	}
	
	.edicao-ativa { 		
 		padding: 5px 5px 5px 20px;
 		border-style: solid; 
 		border-width: 1px; 
 		border-color: #BED6F8;
		border-radius: 	5px 5px 5px 5px; 		
 		
 		position: relative;
 		background: url('/sigaa/ava/img/handle_part.jpg') repeat-y;
 		background-position: 0px 5px;
 	}

</style>

<c:if test="${ not empty noticias }">
	<c:set var="noticiaDestoque" value="${turmaVirtual.ultimaNoticia}" scope="request"/>			
</c:if>

<c:if test="${ requestScope.noticiaDestoque != null }">
	<div class="descricaoOperacao" id="ultimaNoticia">
		<h4> Última Notícia
		<br>${noticiaDestoque.descricao } - <fmt:formatDate value="${noticiaDestoque.data}" pattern="dd/MM/yyyy HH:mm"/> </h4>
		${noticiaDestoque.noticia}
		<br>
		<small> Cadastrado por:  <i> ${noticiaDestoque.usuarioCadastro.pessoa.nome}</i>  </small>
	</div>
</c:if>

<c:if test="${not empty topicoAula.aulas && turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }"  >
		<h:messages/>
		<div class="infoAltRem">
				<h:graphicImage value="/img/refresh-small.png" style="overflow: visible;"/>: Atualizar Arquivo
				<h:graphicImage value="/img/comprovante.png" style="overflow: visible;"/>: Relatório de Acessos 				
				<h:graphicImage value="/ava/img/page_edit.png" style="overflow: visible;"/>: Editar Item
				<h:graphicImage value="/img/porta_arquivos/delete.gif" style="overflow: visible;"/>: Remover Item
				<h:graphicImage value="/img/arrow_left.png" style="overflow: visible;"/>: Mover para Esquerda
		        <h:graphicImage value="/img/arrow_right.png" style="overflow: visible;"/>: Mover para Direita <br/> 
		        <h:graphicImage value="/img/hide.gif" style="overflow: visible;"/>: Esconder Tópico 
				<h:graphicImage value="/img/show.gif" style="overflow: visible;"/>: Mostrar Tópico <br/>
		</div>	
</c:if>

<c:if test="${empty topicoAula.aulas }">
	<c:if test="${turmaVirtual.discente }">
		<%@include file="introDiscente.jsp"%>
	</c:if>
	<c:if test="${turmaVirtual.docente }">
		<%@include file="introDocente.jsp"%>
	</c:if>
</c:if>

<%-- Exibe todos os tópicos de aula ou somente o tópico selecionado. --%>
<a4j:region>

	<h:inputHidden id="idTopicoSelecionado" value="#{ turmaVirtual.topicoSelecionado.id }" />
	<a4j:commandButton id="bExibirTopico" actionListener="#{ turmaVirtual.exibirTopico }" reRender="panelTopicos,navegacaoEsquerda,navegacaoDireita,auxPanelAcoesTopicoAula" style="display:none;" oncomplete="completarCarregamentoTopico();" />

	<c:if test="${ not empty topicoAula.aulas }">
	
		<c:if test="${ turmaVirtual.topicoSelecionado.id <= 0 }">
			<a4j:outputPanel id="panelTopicosNaoSelecionados">
				<a4j:repeat var="aula" value="#{ topicoAula.aulas }">
					<%@include file="/ava/topico_aula.jsp" %>
				</a4j:repeat>
			</a4j:outputPanel>
		</c:if>
		<c:if test="${ turmaVirtual.topicoSelecionado.id > 0 }">
		
			<h:selectOneMenu onchange="escolheTopico();" id="escolherTopico" style="display:none;">
				<c:forEach var="c" items="#{topicoAula.comboIdentado }">
					<f:selectItem itemValue="#{c.value}" itemLabel="#{ c.label }" />
				</c:forEach>
			</h:selectOneMenu>
		
			<%-- Modal Panel contendo o select para o aluno indicar qual aula deseja visualizar. --%>
			<p:dialog id="panelAcoesTopicoAula" widgetVar="panelAcoesTopicoAula" header="Selecione uma aula" width="650" height="400" modal="true" rendered="#{ turmaVirtual.topicoSelecionado.id > 0 }">
				<a4j:outputPanel id="auxPanelAcoesTopicoAula">
					<a4j:repeat var="c" value="#{topicoAula.comboIdentado }">
						<c:set var="selected" value="#{c.value == turmaVirtual.topicoSelecionado.id ? 'checked=checked' : ''}" />
						<h:outputText escape="false" value="<input id='ta#{c.value}' name='ta' type='radio' onclick='auxEscolheTopico(#{c.value});panelAcoesTopicoAula.hide();' #{selected} /> <label for='ta#{c.value}'>#{ c.label }</label><br/><br/>" />
					</a4j:repeat>
					
					<script>J("#formAva\\:escolherTopico").val(<h:outputText value="#{turmaVirtual.topicoSelecionado.id}" />);</script>
				</a4j:outputPanel>
			</p:dialog>
		
		
			<%-- Exibe o menu de navegação para os tópicos de aula --%>
			<div class="naoImprimir" style="position:relative;text-align:center;background:#DFE8F6;padding:5px;">
			
				<%-- Status que indica a atualização do tópico de aula. --%>
				<a4j:status>
					<f:facet name="start" >
						<rich:panel style="position:absolute;left:0px;height:0px;width:100%;height:25px;text-align:center;background:#DFE8F6;padding:0px;margin:0px;border:none;z-index:99">
							<h:graphicImage value="/img/indicator.gif" />
						</rich:panel>
					</f:facet>
				</a4j:status>
				
				<a4j:outputPanel id="navegacaoEsquerda">
					<h:commandLink id="linkEsquerda" onclick="exibeTopicoAnterior();return false;" rendered="#{ turmaVirtual.idTopicoAnterior > 0 }" style="float:left;">
						<h:graphicImage value="/ava/img/bt_esquerda_ativo.png" alt="Visualizar aula anterior" title="Visualizar aula anterior" />
						<f:param name="idTopicoAula" value="#{ turmaVirtual.idTopicoAnterior }" />
					</h:commandLink>
					<h:graphicImage value="/ava/img/bt_esquerda.png" rendered="#{ turmaVirtual.idTopicoAnterior <= 0 }" alt="Não há aulas anteriores" title="Não há aulas anteriores" style="float:left;" />
				</a4j:outputPanel>
				
				<a4j:outputPanel id="navegacaoDireita">
					<h:commandLink id="linkDireita" onclick="exibeProximoTopico();return false;" rendered="#{ turmaVirtual.idProximoTopico > 0 }" style="float:right;">
						<h:graphicImage value="/ava/img/bt_direita_ativo.png" alt="Visualizar próxima aula" title="Visualizar próxima aula"  />
						<f:param name="idTopicoAula" value="#{ turmaVirtual.idProximoTopico }" />
					</h:commandLink>
					<h:graphicImage value="/ava/img/bt_direita.png" rendered="#{ turmaVirtual.idProximoTopico <= 0 }" alt="Não há próximas aulas" title="Não há próximas aulas" style="float:right;" />
				</a4j:outputPanel>
				
				<div id="camada" style="position:absolute;x:0px;left:0px;width:100%:height:100%;background:#000000;display:none;"></div>
				
				<script>var camada = J("#camada");</script>
				
				<p:commandButton id="botaoMenuAulas" value="Escolher Aula" style="height:20px;padding:0px;margin-top:0px;z-index:1;" onclick="panelAcoesTopicoAula.show();camada.css('display', 'block');return false;" image="selecionarAula" />
				
			</div>
		
			<div style="position:relative;">
			
				<div  id="auxAcaoTopico" style="position:absolute;right:10px;top:25px;z-index:999;">
					<c:set var="aula" value="#{turmaVirtual.topicoSelecionado }" />
				</div>
			
				<a4j:outputPanel id="panelTopicos">
					<c:set var="aula" value="#{turmaVirtual.topicoSelecionado }" />
					<%@include file="/ava/topico_aula.jsp" %>
				</a4j:outputPanel>
			</div>
			
			<%-- Exibe o slider caso esteja exibindo paginado --%>
			<%--
			<div style="padding:10px 20px 10px 20px;background:#DFE8F6;position:static;bottom:50px;left:50px;"><div id="sliderPaginacao"></div></div>
			<script>
				select = J("#formAva\\:escolherTopico");
				configurarSlider();
			</script>
			--%>
		</c:if>
	</c:if>
</a4j:region>

<c:if test="${ turmaVirtual.docente  || permissaoAva.permissaoUsuario.docente }">
	<h:inputHidden id="idTopicoAcao" value="#{ topicoAula.acaoTopico }"/>
	<h:commandLink id="realizarAcaoTopico" style="display:none;" action="#{ topicoAula.acaoTopico }">
		<f:param name="paginaOrigem" value="portalPrincipal" />
	</h:commandLink>
</c:if>