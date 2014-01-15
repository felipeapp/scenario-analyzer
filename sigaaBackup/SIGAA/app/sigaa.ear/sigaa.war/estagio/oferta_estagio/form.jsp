<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "550", height : "200", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>
<f:view>
	<h:form id="form">
		<a4j:keepAlive beanName="ofertaEstagioMBean"/>
		<h2> <ufrn:subSistema /> &gt; Oferta de Estágio</h2>
		
		<div class="descricaoOperacao">
			<c:if test="${ofertaEstagioMBean.portalCoordenadorGraduacao}">
			    <p><b>Caro Coordenador(a),</b></p><br/>
			</c:if>
			
			<c:if test="${not ofertaEstagioMBean.portalCoordenadorGraduacao}">
				<p><b>Caro Usuário(a),</b></p><br/>
			</c:if>	           	
			
			<p>Através dessa opção, é possível Cadastrar Ofertas de Estágio. Os discentes dos Cursos para os quais as vagas serão ofertadas
			 podem se candidatar a estas através do Portal do Discente.</p>	
			 <c:if test="${ofertaEstagioMBean.portalConcedenteEstagio}">
				 <p><b>ATENÇÃO!</b> A Oferta de Estágio só ficará disponível para o discente após a aprovação do Coordenador do Curso.</p>
			 </c:if>
		</div>
	
		<table class="visualizacao" style="width: 90%">
			<caption>Dados do Concedente do Estágio</caption>
			<tr>
				<th width="30%">CNPJ:</th>
				<td>
					<h:outputText value="#{ofertaEstagioMBean.obj.concedente.pessoa.cpfCnpjFormatado}"/>
				</td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td>
					<h:outputText value="#{ofertaEstagioMBean.obj.concedente.pessoa.nome}"/>																																				
				</td>
			</tr>
			<tr>
				<th>Responsável:</th>
				<td>
					<h:outputText value="#{ofertaEstagioMBean.obj.concedente.responsavel.pessoa.nome}"/>
				</td>
			</tr>
			<tr>
				<th>Tipo do Convênio:</th>
				<td>
					<h:outputText value="#{ofertaEstagioMBean.obj.concedente.convenioEstagio.tipoConvenio.descricao}"/>
				</td>
			</tr>
			<tr>
				<th>O Concedente é um Órgão Federal:</th>
				<td>
					<ufrn:format type="simnao" valor="${ofertaEstagioMBean.obj.concedente.convenioEstagio.orgaoFederal}"></ufrn:format>
				</td>
			</tr>
		</table>
		
		<br/>
			
		<table class="formulario" style="width: 90%">
			<caption>Informe do dados da Oferta de Estágio</caption>
			<tr>
				<th class="obrigatorio"  style="width: 20%;">Título:</th>
				<td colspan="3">
					<h:inputText id="titulo" value="#{ofertaEstagioMBean.obj.titulo}" onkeyup="CAPS(this);" maxlength="200" size="80"/>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Número de Vagas:</th>
				<td colspan="3">
					<h:inputText value="#{ofertaEstagioMBean.obj.numeroVagas}" id="numeroVagas" size="5" maxlength="6" title="Número de Vagas" 
						onkeyup="return formatarInteiro(this);" style="text-align: right;"/> 				
				</td>
			</tr>
			<tr>
				<th class="${ ofertaEstagioMBean.bolsaObrigatoria ? 'obrigatorio':''}">Valor da Bolsa:</th>		
				<td>
					<h:inputText value="#{ofertaEstagioMBean.obj.valorBolsa}" id="valorBolsa" size="10" maxlength="10" 
						onkeypress="return(formataValor(this, event, 2))" style="text-align: right;">
						<f:converter converterId="convertMoeda"/>
					</h:inputText>			
				</td>
				<th  style="width: 20%;" class="${ ofertaEstagioMBean.auxilioTransporteObrigatorio ? 'obrigatorio':''}">Aux. Transporte:</th>
				<td>
					<h:inputText value="#{ofertaEstagioMBean.obj.valorAuxTransporte}" id="valorAuxTransporte" size="5" maxlength="5" 
						onkeypress="return(formataValor(this, event, 2))" style="text-align: right;">
						<f:converter converterId="convertMoeda"/>
					</h:inputText>  ao dia
				</td>	
			</tr>
			<tr>
				<th class="obrigatorio">Início da Publicação:</th>
				<td>
					<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
							maxlength="10" onkeypress="return formataData(this,event)" value="#{ofertaEstagioMBean.obj.dataInicioPublicacao}" id="dataInicioPublicacao" 
							title="Início da Publicação"/> 							
				</td>
				<th>Fim da Publicação:<span class="obrigatorio">&nbsp;</span></th>
				<td>
					<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
							maxlength="10" onkeypress="return formataData(this,event)" value="#{ofertaEstagioMBean.obj.dataFimPublicacao}" id="dataFimPublicacao" 
							title="Fim da Publicação"/> 							
				</td>
			</tr>		

			<tr>
				<td class="subFormulario" colspan="4">Cursos para os quais as vagas serão Ofertadas</td>			
			</tr>			
			
			<a4j:region rendered="#{!ofertaEstagioMBean.selecionaCurso}">
				<tr>
					<th style="font-weight: bold;">Curso:</th>
					<td colspan="3">${ofertaEstagioMBean.cursoAtualCoordenacao.nomeCompleto}</td>
				</tr>
			</a4j:region>
				
			<a4j:region rendered="#{ofertaEstagioMBean.selecionaCurso}">
				<tr>
					<th class="obrigatorio">Curso:</th>
					<td colspan="3">
						<a4j:outputPanel id="inputCurso">
							<h:inputText value="#{ofertaEstagioMBean.curso.nome}" onkeyup="CAPS(this);" id="nomeCurso" style="width: 500px;"/>
							<rich:spacer width="10"/> 
							<a4j:commandLink title="Adicionar Curso" id="addCurso" reRender="cursosOfertados, inputCurso" actionListener="#{ofertaEstagioMBean.adicionarCurso}">
								<h:graphicImage value="/img/adicionar.gif"/>
							</a4j:commandLink>
					        
							<rich:suggestionbox id="sbCurso" width="400" height="120" for="nomeCurso" 
								minChars="3" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200"
								suggestionAction="#{curso.autocompleteNomeGeralCursos}"  var="_curso" fetchValue="#{_curso.nome}">
								<h:column>
									<h:outputText value="#{_curso.descricao}"/>
								</h:column>
								<a4j:support event="onselect">
									<f:setPropertyActionListener value="#{_curso}" target="#{ofertaEstagioMBean.curso}"/>
								</a4j:support>
							</rich:suggestionbox>	
				            <a4j:status id="statusCurso">
				                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
				            </a4j:status>
				        </a4j:outputPanel>
					</td>
				</tr>
		
				<tr>
					<td colspan="4">
						<a4j:outputPanel id="cursosOfertados">
							<c:if test="${not empty ofertaEstagioMBean.obj.cursosOfertados}">
								<div class="infoAltRem">
							        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Curso
								</div>					
							</c:if>
						
							<t:dataTable value="#{ofertaEstagioMBean.obj.cursosOfertados}" var="_curso" style="width: 90%;"  id="datatable_curso"
								styleClass="listagem" rowClasses="linhaPar, linhaImpar" rendered="#{not empty ofertaEstagioMBean.obj.cursosOfertados}">
										
								<t:column>
									<f:facet name="header"><f:verbatim>Cursos</f:verbatim></f:facet>
									<h:outputText value="#{_curso.nomeCompleto}"/>
								</t:column>
				
								<t:column width="5%" styleClass="centerAlign">
									<a4j:commandLink actionListener="#{ofertaEstagioMBean.removerCurso}" onclick="if (!confirm('Confirma a remoção desta informação?')) return false;" id="remover_curso"
										title="Remover Curso" reRender="cursosOfertados">
											<h:graphicImage value="/img/delete.gif"/>
											<f:attribute name="curso" value="#{_curso}"/>
									</a4j:commandLink>
								</t:column>					
							</t:dataTable>
						</a4j:outputPanel>
					</td>			
				</tr>		
           </a4j:region>	
          	<tr>
				<td class="subFormulario" colspan="4">Descrição da Oferta<span class="obrigatorio"></span></td>
			</tr>
			<tr>
				<td></td>
				<td colspan="3">
					<h:inputTextarea rows="8" id="descricao" cols="80" value="#{ofertaEstagioMBean.obj.descricao}" />				
				</td>
			</tr>	
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="Cancelar" action="#{ofertaEstagioMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancel"/>					
						<h:commandButton value="Próximo >>" action="#{ofertaEstagioMBean.confirmarDados}" id="btProximo"/>
					</td>
				</tr>
			</tfoot>
		</table>
	
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>