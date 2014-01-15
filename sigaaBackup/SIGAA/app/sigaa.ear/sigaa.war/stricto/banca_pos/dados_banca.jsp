<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
	tinyMCE.init({
    	mode : "textareas",
        theme : "advanced",
        width : "400",
        height : "300",
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
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema/> >  Banca de Pós > Dados Gerais</h2>

	<h:form id="form"  enctype="multipart/form-data">
		<c:set value="#{bancaPos.discente}" var="discente"></c:set>
		<%@include file="/graduacao/info_discente.jsp"%>
		
		<div class="descricaoOperacao">
		<t:htmlTag value="div" rendered="#{ bancaPos.obj.id != 0 }">
 			<b>Atenção!</b> Caro usuário(a), qualquer alteração realizada no cadastro 
 			da banca será enviada por e-mail para os alunos do programa e para a equipe.
		</t:htmlTag>
		
		<t:htmlTag value="div" rendered="#{ bancaPos.obj.id == 0 }">
		 	<b>Atenção!</b> Caro usuário(a), as informações do cadastro da banca serão
		 	enviadas por e-mail para os alunos do programa e para a equipe.
		</t:htmlTag>
		</div>

		<br/>
		<table class="formulario" width="90%">
			<caption class="formulario">Dados da Banca</caption>
			<c:if test="${bancaPos.obj.matriculaComponente != null}" >
				<tr>
					<th width="20%" class="${bancaPos.escolheAtividade ? 'required' : 'rotulo'}">Atividade Matriculada:</th>
					<td>
						<c:if test="${not bancaPos.escolheAtividade}" >
							${bancaPos.obj.matriculaComponente.componenteDescricao} (${bancaPos.obj.matriculaComponente.anoPeriodo})
						</c:if>
						<c:if test="${bancaPos.escolheAtividade}">
							<h:selectOneMenu value="#{bancaPos.obj.matriculaComponente.id}" id="matriculaComponente">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{bancaPos.matriculasComponenteCombo}" id="itensMatriculasCombo"/>
							</h:selectOneMenu>
						</c:if>
					</td>
				</tr>
			</c:if>
			<tr>
				<th width="20%"><b>Tipo:</b></th>
				<td>${bancaPos.obj.tipoDescricao }</td>
			</tr>
			<tr>
				<th class="required">Local:</th>
				<td>
				<h:inputText value="#{bancaPos.obj.local}" id="local" size="70" maxlength="100">
				</h:inputText>
				</td>
			</tr>	
			<tr>
				<td colspan="2">
					<table class="subFormulario" style="width: 100%">
						<caption>Dados do Trabalho</caption>
						<tr>
							<th width="7%" valign="center">Título:<span class="required">&nbsp;</span></th>
							<td colspan="2">
								<h:inputTextarea value="#{bancaPos.obj.dadosDefesa.titulo}" rows="5" cols="80" id="titulo"/>
							</td>
						</tr>
						<tr>
							<th>Páginas:<span class="required">&nbsp;</span></th>
							<td colspan="2">
								<h:inputText id="paginas" value="#{bancaPos.obj.dadosDefesa.paginas}" size="4" maxlength="6" onkeypress="formatarInteiro(this); return ApenasNumeros(event);"></h:inputText>
							</td>
						</tr>
						<tr>
							<th>Data:<span class="required">&nbsp;</span></th>
							<td colspan="2">
								 <t:inputCalendar 
								 	id="Dia" value="#{bancaPos.obj.data}" 
								 	size="10" 
								 	maxlength="10" 
								 	onkeypress="return(formatarMascara(this,event,'##/##/####'))"
									renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy">
									
									<f:converter converterId="convertData"/>
									
								</t:inputCalendar>
							</td>
						</tr>
						<tr>
							<th>Hora:<span class="required">&nbsp;</span></th>
							<td colspan="2">
								<h:inputText id="hora" value="#{bancaPos.obj.hora}" maxlength="5" size="6"
									onkeypress="return(formataHora(this, event))" title="Hora">
									<f:converter converterId="convertHora"/>									
								</h:inputText> (HH:mm)
							</td>
						</tr>
						<tr>
							<th nowrap="nowrap">Grande Área:<span class="required">&nbsp;</span></th>
							<td colspan="2">
							<a4j:region>
							<h:selectOneMenu value="#{bancaPos.grandeArea.id}" valueChangeListener="#{bancaPos.carregaAreas}"
								id="grandeArea" style="width: 300px"
								 immediate="true">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{area.allCombo}" id="areasCombo"/>
								<a4j:support event="onchange" reRender="area, subArea, especialidade"></a4j:support>
							</h:selectOneMenu>&nbsp;
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
							</a4j:region>
							</td>
						</tr>
						<tr>
							<th>Área:<span class="required">&nbsp;</span></th>
							<td colspan="2">
							<a4j:region>
							<h:selectOneMenu value="#{bancaPos.area.id}" valueChangeListener="#{bancaPos.carregaSubAreas}"
								id="area" style="width: 300px"
								 immediate="true">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{bancaPos.areas}" />
								<a4j:support event="onchange" reRender="subArea, especialidade"></a4j:support>
							</h:selectOneMenu>&nbsp;
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
							</a4j:region>
							</td>
						</tr>
						<tr>
							<th>Sub-Área:</th>
							<td colspan="2">
							<a4j:region>
							<h:selectOneMenu value="#{bancaPos.subArea.id}" valueChangeListener="#{ bancaPos.carregaEspecialidades }"
								id="subArea" style="width: 300px" 
								immediate="true">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{bancaPos.subAreas}" id="ItensSubAreas"/>
								<a4j:support event="onchange" reRender="especialidade"></a4j:support>
							</h:selectOneMenu>&nbsp;
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
							</a4j:region>
							</td>
						</tr>
						<tr>
							<th>Especialidade:</th>
							<td colspan="2">
							<h:selectOneMenu value="#{bancaPos.especialidade.id}" style="width: 300px" id="especialidade">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{bancaPos.especialidades}"/>
							</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th valign="center">Resumo:<span class="required">&nbsp;</span></th>
							<td colspan="2">
							<h:inputTextarea rows="10" id="dadosResumo" cols="80" value="#{bancaPos.obj.dadosDefesa.resumo}" />
							</td>
						</tr>
						<tr>
							<th valign="center">Palavras Chave:<span class="required">&nbsp;</span></th>
							<td colspan="2">
							<h:inputTextarea id="palavrasChave" rows="10" cols="80" value="#{bancaPos.obj.dadosDefesa.palavrasChave}" onkeyup="this.value = this.value.substring(0, 180);"/>
							</td>
						</tr>
							
						<%--
						<c:if test="${bancaPos.defesaAntiga}">
							<tr>
								<th width="16%">Arquivo:<span class="required">&nbsp;</span></th>
								<td colspan="2"><t:inputFileUpload id="arquivo" value="#{bancaPos.arquivo}"   size="40"/></td>
							</tr>
						--%>
							<tr>
								<th>Link do Arquivo (BDTD):</th>
								<td colspan="2">
									<h:inputText id="linkArquivo" value="#{bancaPos.obj.dadosDefesa.linkArquivo}" maxlength="400" size="80"  />
									<ufrn:help img="/img/ajuda.gif">Link de acesso a defesa no BDTD da ${ configSistema['siglaInstituicao'] }.</ufrn:help>
								</td>
							</tr>
						<%--							
						</c:if>
						 --%>
					</table>
				
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Cancelar" id="cancelamento"
						action="#{bancaPos.cancelar}"  onclick="#{confirm}"/>
					<h:commandButton id="confir" value="Próximo Passo >> "
						action="#{bancaPos.submeterDadosGerais}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

	<c:if test="${not empty bancaPos.bancasDoDiscente}">

		<table class="subFormulario" style="width: 100%" >
			<caption>Outras Bancas desse Discente</caption>
			<thead>
				<tr>
				<td width="10%">Tipo</td>
				<td>Descrição</td>
				<td>Atividade Matriculada</td>
				<td>Situação</td>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${bancaPos.bancasDoDiscente}" var="b" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td>${b.tipoDescricao}</td>
				<td>${b.dadosDefesa.descricao}</td>
				<td>${b.matriculaComponente.componenteDescricao}</td>
				<td>
					<c:choose>
						<c:when test="${b.cancelada || b.pendente}">
							${b.descricaoStatus}
						</c:when>
						<c:when test="${not empty b.matriculaComponente}">
							${b.matriculaComponente.situacaoMatricula.descricao}
						</c:when>
						<c:otherwise>
							${b.descricaoStatus}
						</c:otherwise>
					</c:choose>					
				</td>
			</tr>
			</c:forEach>
			</tbody>
		</table>

	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
