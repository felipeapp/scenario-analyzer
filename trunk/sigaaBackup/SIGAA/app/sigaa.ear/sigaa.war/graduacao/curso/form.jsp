<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
	tinyMCE.init({
    	mode : "textareas",
        theme : "advanced",
        width : "400",
        height : "200",
        language : "pt",
        theme_advanced_toolbar_location : "top",
        plugins : "table, preview, iespell, print, fullscreen, advhr, directionality, searchreplace, insertdatetime, paste",
        theme_advanced_buttons1 : "fullscreen,separator,preview,separator,cut,copy,paste,separator,undo,redo,separator,search,replace,separator,code,separator,cleanup,separator,bold,italic,underline,strikethrough,separator,forecolor,backcolor,separator,justifyleft,justifycenter,justifyright,justifyfull,separator,help",
        theme_advanced_buttons2 : "removeformat,styleselect,formatselect,fontselect,fontsizeselect,separator,bullist,numlist,outdent,indent,separator,link,unlink,anchor",
        theme_advanced_buttons3_add : "pastetext,pasteword,selectall",
    	paste_create_paragraphs : false,
    	paste_create_linebreaks : false,
    	paste_use_dialog : true,
    	paste_auto_cleanup_on_paste : true,
    	paste_convert_middot_lists : false,
    	paste_unindented_list_class : "unindentedList",
    	paste_convert_headers_to_strong : true,
    	plugin_preview_width : "400",
        plugin_preview_height : "300",
        fullscreen_settings : {
        	theme_advanced_path_location : "top"
        },
        extended_valid_elements : "hr[class|width|size|noshade]",
        plugin_insertdate_dateFormat : "%Y-%m-%d",
        plugin_insertdate_timeFormat : "%H:%M:%S"
	});

</script>

<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>

<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp" %>
	<h:outputText value="#{cursoGrad.create }"></h:outputText>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Cursos</h2>

	<h:form id="cadastroCurso" enctype="multipart/form-data">
		<h:inputHidden value="#{cursoGrad.obj.id}" />
		<table class="formulario" width="99%">
			<caption class="formulario">Dados do Curso</caption>
			<tr>
				<th width="30%" class="required">Nome:</th>
				<td>
					<h:inputText id="nome" value="#{cursoGrad.obj.nome}" size="70"  maxlength="80" onkeyup="CAPS(this)" 
						disabled="#{cursoGrad.readOnly}" readonly="#{cursoGrad.readOnly}" />
				</td>
			</tr>
			<c:if test="${cursoGrad.stricto}">
				<tr>
					<th class="required">Nível:</th>
					<td>
						<h:selectOneRadio id="nivel" onclick="submit()" valueChangeListener="#{cursoGrad.carregarTiposStricto}"
							value="#{cursoGrad.obj.nivel}" disabled="#{cursoGrad.readOnly}">
							<f:selectItems value="#{nivelEnsino.strictoCombo}" />
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<th class="required">Categoria:</th>
					<td>
						<h:selectOneMenu id="tipoStricto" disabled="#{cursoGrad.readOnly}" value="#{cursoGrad.obj.tipoCursoStricto.id}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{cursoGrad.tiposStricto}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
				<th width="30%">Portaria MEC:</th>
				<td>
					<h:inputText id="portariaMec" value="#{cursoGrad.obj.reconhecimentoPortaria}" size="70"  maxlength="80" 
						disabled="#{cursoGrad.readOnly}" readonly="#{cursoGrad.readOnly}" />
				</td>
				</tr>
				<tr>
				<th width="30%">Data de Publicação:</th>
				<td>
					 <t:inputCalendar value="#{cursoGrad.obj.dou}" id="dataPublicacao" size="10" maxlength="10" 
					    onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
					    renderAsPopup="true" renderPopupButtonAsImage="true" 
					    disabled="#{cursoGrad.readOnly}" readonly="#{cursoGrad.readOnly}" > 
					      <f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
				</tr>
				<tr>
				<th width="30%" class="required">Data de Início de Funcionamento:</th>
				<td>
					 <t:inputCalendar value="#{cursoGrad.obj.dataDecreto}" id="dataInicioFuncionamento" size="10" maxlength="10" 
					    onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
					    renderAsPopup="true" renderPopupButtonAsImage="true"
					    disabled="#{cursoGrad.readOnly}" readonly="#{cursoGrad.readOnly}" > 
					      <f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
				</tr>
				<tr>
					<th>Código CAPES do Curso:</th>
					<td>
						<h:inputText id="codCapes" value="#{cursoGrad.obj.codigo}" size="20" maxlength="20" 
							disabled="#{cursoGrad.readOnly}" onkeyup="CAPS(this)" />
					</td>
				</tr>
				<tr>
					<th class="required">Código CAPES do Programa:</th>
					<td>
						<h:inputText id="codCapesProg" value="#{cursoGrad.obj.codProgramaCAPES}" size="20"
							disabled="#{cursoGrad.readOnly}" maxlength="20" onkeyup="CAPS(this)" />
					</td>
				</tr>
				<tr>
					<th class="required">Organização Administrativa:</th>
					<td>
							<h:selectOneMenu id="organizacaoAdministrativa" disabled="#{cursoGrad.readOnly}"
								value="#{cursoGrad.obj.organizacaoAdministrativa.id}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{organizacaoAdministrativa.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>${cursoGrad.stricto ? 'Regimento do Curso:' : 'Projeto Político-Pedagógico:'}</th>
					<td><t:inputFileUpload value="#{ cursoGrad.arquivo }" id="projetoStricto"/></td>
				</tr>
			</c:if>
			<tr>
				<th class="${cursoGrad.stricto ? 'required' : ''}">Titulação para o Gênero Masculino:</th>
				<td>
					<h:inputText id="titulacaoMasculino" value="#{cursoGrad.obj.titulacaoMasculino}"
						size="70" maxlength="120" disabled="#{cursoGrad.readOnly}"
						readonly="#{cursoGrad.readOnly}" />
					<ufrn:help>
						${cursoGrad.stricto ? 'Titulação dada ao discente ao concluir o programa. Por exemplo: Mestre em Ciência Política, Doutor em Literatura Brasileira.' 
							: 'Titulação dada ao discente ao concluir o curso, em especial nos casos cujo grau acadêmico é de formação específica. Por exemplo: Engenheiro de Computação, Arquiteto, etc.'}
					</ufrn:help>
				</td>
			</tr>
			<tr>
				<th class="${cursoGrad.stricto ? 'required' : ''}">Titulação para o Gênero Feminino:</th>
				<td>
					<h:inputText id="titulacaoFeminino" value="#{cursoGrad.obj.titulacaoFeminino}"
						size="70" maxlength="120" disabled="#{cursoGrad.readOnly}"
						readonly="#{cursoGrad.readOnly}" />
					<ufrn:help>
						${cursoGrad.stricto ? 'Titulação dada ao discente ao concluir o programa. Por exemplo: Mestre em Ciência Política, Doutor em Literatura Brasileira.' 
							: 'Titulação dada ao discente ao concluir o curso, em especial nos casos cujo grau acadêmico é de formação específica. Por exemplo: Engenheiro de Computação, Arquiteto, etc.'}. 
							A titulação deverá ser informada para o masculino e feminino. 
					</ufrn:help>
				</td>
			</tr>
			<c:if test="${!cursoGrad.stricto}">
				<tr>
					<th>Código INEP:</th>
					<td>
						<h:inputText id="codigoINEP" value="#{cursoGrad.obj.codigoINEP}" size="10"
							disabled="#{cursoGrad.readOnly}" maxlength="10" onkeyup="CAPS(this)" />
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="required">Estado de Andamento do Curso:</th>
				<td>
					<h:selectOneMenu value="#{municipio.obj.unidadeFederativa.id}" id="uf" disabled="#{cursoGrad.readOnly}"
						converter="#{intConverter}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{unidadeFederativa.allCombo}"/>
						<a4j:support event="onchange" reRender="municipio"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Município de Andamento do Curso:</th>
				<td>
					<a4j:keepAlive beanName="municipio"/>
					<h:selectOneMenu value="#{cursoGrad.obj.municipio.id}" id="municipio" disabled="#{cursoGrad.readOnly}"
						converter="#{intConverter}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{municipio.municipiosByUF}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Área do Curso:</th>
				<td>
					<h:selectOneMenu id="area" value="#{cursoGrad.obj.areaCurso.id}" disabled="#{cursoGrad.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{area.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Forma de Participação do Aluno:</th>
				<td>
					<h:selectOneMenu id="modalidade" value="#{cursoGrad.obj.modalidadeEducacao.id}" disabled="#{cursoGrad.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{modalidadeEducacao.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>

			<c:if test="${!cursoGrad.stricto}">
				<tr>
					<th class="required">Area Sesu:</th>
					<td><h:selectOneMenu id="areaSesu" value="#{cursoGrad.obj.areaSesu.id}"
						readonly="#{cursoGrad.readOnly}" disabledClass="#{habilitacaoGrad.cursoGrad}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{areaSesu.allCombo}" />
					</h:selectOneMenu></td>
				</tr>			
				<tr>
					<th class="required">Área de Conhecimento do Vestibular:</th>
					<td>
						<h:selectOneMenu id="areaVestibular" value="#{cursoGrad.obj.areaVestibular.id}" disabled="#{cursoGrad.readOnly}"
							style="width:80%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{areaConhecimentoVestibular.allAtivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required">Natureza do Curso</th>
					<td>
						<h:selectOneMenu id="natureza" value="#{cursoGrad.obj.naturezaCurso.id}" disabled="#{cursoGrad.readOnly}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{naturezaCurso.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="required">${cursoGrad.stricto ? 'Periodicidade de Ingresso:' : 'Tipo de Oferta do Curso:'}</th>
				<td>
					<h:selectOneMenu id="tipoOfertaCurso" value="#{cursoGrad.obj.tipoOfertaCurso.id}" disabled="#{cursoGrad.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{tipoOfertaCurso.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Tipo de Oferta de Disciplina:</th>
				<td>
					<h:selectOneMenu id="tipoOfertaDisciplina" value="#{cursoGrad.obj.tipoOfertaDisciplina.id}" disabled="#{cursoGrad.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{tipoOfertaDisciplina.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${!cursoGrad.stricto}">
			<tr>
				<th class="required">Tipo de Ciclo de Formação:</th>
				<td>
					<h:selectOneMenu id="tipoCicloFormacao" value="#{cursoGrad.obj.tipoCicloFormacao.id}" disabled="#{cursoGrad.readOnly}">
						<f:selectItems value="#{tipoCicloFormacaoMBean.allCombo}" />
					</h:selectOneMenu>					
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Convênio Acadêmico:</th>
				<td>
					<h:selectOneMenu id="convenio"
						value="#{cursoGrad.obj.convenio.id}"
						disabled="#{cursoGrad.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{convenioAcademico.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${not cursoGrad.readOnly }">
				<tr>
					<th class="required">Unidade Responsável:</th>
					<td>
						<h:selectOneMenu id="unidade" value="#{cursoGrad.obj.unidade.id}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<c:if test="${!cursoGrad.stricto}">
								<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
							</c:if>
							<c:if test="${cursoGrad.stricto}">
								<f:selectItems value="#{unidade.allProgramaPosCombo}" />
							</c:if>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Unidade Responsável 2:</th>
					<td>
						<h:selectOneMenu id="unidade2"
							value="#{cursoGrad.obj.unidade2.id}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<c:if test="${!cursoGrad.stricto}">
								<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
							</c:if>
							<c:if test="${cursoGrad.stricto}">
								<f:selectItems value="#{unidade.allProgramaPosCombo}" />
							</c:if>
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${not cursoGrad.stricto}">
				<tr>
					<th class="required">Unidade da Coordenação:</th>
					<td>
						<h:selectOneMenu id="unidadeCoordenacao" value="#{cursoGrad.obj.unidadeCoordenacao.id}" disabled="#{cursoGrad.readOnly}" style="width:80%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.allCoordenacaoCursoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${!cursoGrad.stricto}">
				
				<tr>
					<th>Website do curso:</th>
					<td><h:inputText id="website" value="#{cursoGrad.obj.website}" disabled="#{cursoGrad.readOnly}" size="70" /></td>
				</tr>				
			</c:if>
			<tr>
				<th>Coordenador Pode Matricular Discente:</th>
				<td>
					<h:selectBooleanCheckbox id="checkSolicitacaoTurma"
						value="#{cursoGrad.obj.podeMatricular}" styleClass="noborder" disabled="#{cursoGrad.confirmButton eq 'Remover'}" /> <ufrn:help
						img="/img/ajuda.gif">Marque esta opção caso seja possível criar que o coordenador realize matrícula em componentes
					de alunos deste curso.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Ativo:</th>
				<td>
					<h:selectBooleanCheckbox id="checkAtivo"
						value="#{cursoGrad.obj.ativo}" styleClass="noborder" disabled="#{cursoGrad.confirmButton eq 'Remover'}"/> <ufrn:help
						img="/img/ajuda.gif">Marque esta opção para Ativar ou Desativar o curso.</ufrn:help>
				</td>
			</tr>
			<c:if test="${!cursoGrad.stricto}">
			<tr>
				<td colspan="2">
					<table width="100%" class="subFormulario">
						<caption>Projeto Político-Pedagógico</caption>
						<tr>
							<th>${!cursoGrad.stricto ? 'Projeto Político-Pedagógico:' : 'Regimento do Curso:' }</th>
							<td><t:inputFileUpload value="#{ cursoGrad.arquivo }"  id="projetoGraduacao"/></td>
						</tr>
						<tr>
							<th width="24%" valign="top">Perfil do Profissional:</th>
							<td><h:inputTextarea id="perfilProfissional" value="#{cursoGrad.obj.perfilProfissional}" disabled="#{cursoGrad.readOnly}" rows="5" cols="60"/></td>
						</tr>
						
						<tr>
							<th width="24%" valign="top">Área de Atuação:</th>
							<td><h:inputTextarea id="camposAtuacao" value="#{cursoGrad.obj.campoAtuacao}" disabled="#{cursoGrad.readOnly}" rows="5" cols="60" /></td>
						</tr>
						
						<tr>
							<th width="24%" valign="top">Competências e Habilidades do Profissional:</th>
							<td><h:inputTextarea id="compentenciasHabilidades" value="#{cursoGrad.obj.competenciasHabilidades}" disabled="#{cursoGrad.readOnly}" cols="67" /></td>
						</tr>
						<tr>
							<th width="24%" valign="top">Metodologia:</th>
							<td><h:inputTextarea id="metodologia" value="#{cursoGrad.obj.metodologia}" disabled="#{cursoGrad.readOnly}" cols="67" /></td>
						</tr>
						<tr>
							<th width="24%" valign="top">Sistema de Gestão do Curso:</th>
							<td><h:inputTextarea id="gestaoCurso" value="#{cursoGrad.obj.gestaoCurso}" disabled="#{cursoGrad.readOnly}" cols="67" /></td>
						</tr>
						<tr>
							<th width="24%" valign="top">Avaliação do Curso:</th>
							<td><h:inputTextarea id="avaliacaoCurso" value="#{cursoGrad.obj.avaliacaoCurso}" disabled="#{cursoGrad.readOnly}" cols="67" /></td>
						</tr>
					</table>
				</td>	
			</tr>
			</c:if>
			<%-- Será Inserido novamente no término da tarefa 28682 
			<c:if test="${cursoGrad.stricto}">
				<tr>
					<th> Curso em Rede</th>
					<td>
						<h:selectBooleanCheckbox id="checkCursoRede" 
							value="#{cursoGrad.obj.rede}" styleClass="noborder" onclick="escolherInstituicao(this)" /> 
							<ufrn:help img="/img/ajuda.gif">Marque esta opção caso este curso seja em rede com outras Instituições de ensino.</ufrn:help>
					</td>
				</tr>
				
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="75%" id="instituicao">
							<tr>
								<td>
									<br />
									<center>
										<div class="infoAltRem" style="width:100%;"> 
											<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />: Adicionar Instituição de Ensino
										</div>
									</center>
									<table class="subFormulario" width="100%" >
									<caption>Instituição(ões) de Ensino em Rede:</caption>
										<tr>
											<th class="required">Instituição de Ensino:</th>
											<td>
												<h:selectOneMenu value="#{cursoGrad.instituicaoEnsino.id}" id="selectInstituicaoEnsino">
													<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
													<f:selectItems value="#{instituicoesEnsino.allCombo}"/>
												</h:selectOneMenu>
											</td>
											<td>
												<a4j:commandLink  id="add_instituicao" title="Adicionar Instituição de Ensino" >
													<h:graphicImage url="/img/adicionar.gif" id="img_add_instituicao" />
													<a4j:support reRender="listaInstituicoes" actionListener="#{cursoGrad.adicionarInstituicao}" event="onclick" ></a4j:support>
												</a4j:commandLink>
												
											</td>
										</tr>				
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									
										<div class="infoAltRem">
											<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
											Remover Instituição de Ensino
										</div>
										<a4j:outputPanel id="listaInstituicoes">
										<c:if test="${not empty cursoGrad.instituicoesEnsino}">
										<t:dataTable var="instEnsino" styleClass="listagem;" rowClasses="linhaPar,linhaImpar" value="#{ cursoGrad.instituicoesEnsino }" rowIndexVar="row" id="tableInstituicoes" width="100%">						
											<f:facet name="caption"><h:outputText value="Instituições de Ensino"/></f:facet>
											<f:facet name="header"><h:outputText value="Nome"/></f:facet>
											
											<t:column>
												<h:outputText value="#{instEnsino.nome}" />
											</t:column>
											<t:column>
												<h:commandLink action="#{cursoGrad.removerInstituicao}" title="Remover Instituição de Ensino">
													<h:graphicImage url="/img/delete.gif" />
													<f:param name="indice" value="#{row}"/>
												</h:commandLink>	
											</t:column>
										</t:dataTable>
										</c:if>
										</a4j:outputPanel>					
											
								</td>		
							</tr>
						</table>	
					</td>
				</tr>
			</c:if>
			 --%>
			
			<tr>
				<td colspan="2">
					<c:set var="exibirApenasSenha" value="true" scope="request" />
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</td>
			</tr>		
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{cursoGrad.confirmButton}" action="#{cursoGrad.cadastrar}" id="cadastrar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{cursoGrad.cancelarForm}" immediate="true" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>


	</h:form>
	<br>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	<br>

<script type="text/javascript">

	$('cadastroCurso:nome').focus();

	function escolherInstituicao(rede) {
		if (rede.checked) {
			$('instituicao').show();
		} else {
			$('instituicao').hide();
		}
		$('cadastroCurso:checkCursoRede').focus();
	}
	
	escolherInstituicao($('cadastroCurso:checkCursoRede'));
	
</script>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
