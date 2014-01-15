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
<f:view>
	<a4j:keepAlive beanName="membroBancaMBean"/>
	<a4j:keepAlive beanName="buscaBancaDefesaMBean"/>
	<a4j:keepAlive beanName="bancaDefesaMBean"/>
	<h2 class="title"><ufrn:subSistema/> &gt; Banca de Defesa</h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p>
		<br/>
		<p>Nesta tela você poderá informar os dados da banca do discente selecionado, 
		assim como título do trabalho, local, data e hora de sua realização.</p>		
	</div>
	
	<h:form id="form">
		
		<c:set value="#{bancaDefesaMBean.obj.discente}" var="discente"/>
		<table class="visualizacao" style="width: 90%">
			<tr>
				<td width="14%"></td>
				<th width="3%" style="text-align: right;">Matrícula:</th>
				<td style="text-align: left;">${discente.matricula }</td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: right;"> Discente: </th>
				<td style="text-align: left;"> ${discente.pessoa.nome } </td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: right;"> Curso: </th>
				<td style="text-align: left;"> ${discente.curso.nomeCursoStricto } </td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: right;"> Status: </th>
				<td style="text-align: left;"> ${discente.statusString } </td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: right;">Tipo:</th>
				<td style="text-align: left;"> ${discente.tipoString } </td>
			</tr>
			<tr>
				<td></td>
				<th> Orientador: </th>
				<td>
					<h:outputText value="#{bancaDefesaMBean.obj.orientacaoAtividade.orientador.nome}" 
						rendered="#{not empty bancaDefesaMBean.obj.orientacaoAtividade}" id="outputTextOrientador"/>
					<h:outputText value="Não informado" rendered="#{empty bancaDefesaMBean.obj.orientacaoAtividade}" id="outputTextNaoInformado"/>
				</td>
			</tr>			
		</table>	
		<br/>			
		
		<table class="formulario" width="90%">
			<caption class="formulario">Dados da Banca</caption>
			
			<c:if test="${bancaDefesaMBean.matriculasComponenteCombo != null}" >
				<tr>
					<th width="20%" class="obrigatorio">Atividade Matriculada:</th>
					<td>
						<h:selectOneMenu value="#{bancaDefesaMBean.obj.matriculaComponente.id}" id="matriculaComponente">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{bancaDefesaMBean.matriculasComponenteCombo}" id="itensMatriculasCombo"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>			
			<tr>
				<th class="required">Local:</th>
				<td>
					<h:inputText value="#{bancaDefesaMBean.obj.local}" id="local" size="70" maxlength="100"/>
				</td>
			</tr>	
			<tr>
				<td colspan="2">
					<table class="subFormulario" style="width: 100%">
						<caption>Dados do Trabalho</caption>
						<tr>
							<th width="20%" class="obrigatorio">Título:</th>
							<td colspan="2">
								<h:inputTextarea value="#{bancaDefesaMBean.obj.titulo}" rows="5" cols="80" id="titulo"/>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Páginas:</th>
							<td colspan="2">
								<h:inputText id="paginas" value="#{bancaDefesaMBean.obj.paginas}" size="4" onkeyup="return(formatarInteiro(this))"/>
							</td>
						</tr>						
						<tr>
							<th class="obrigatorio">Data:</th>
							<td colspan="2">
								 <t:inputCalendar 
								 	id="Dia" value="#{bancaDefesaMBean.obj.dataDefesa}" 
								 	size="10" 
								 	maxlength="10" 
								 	onkeypress="return(formatarMascara(this,event,'##/##/####'))"
									renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy">
									
									<f:converter converterId="convertData"/>
									
								</t:inputCalendar>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Hora:</th>
							<td colspan="2">
								<h:inputText id="hora" value="#{bancaDefesaMBean.obj.hora}" 
									maxlength="5" size="6" requiredMessage="required"
									converterMessage="Hora: Campo com formato inválido."
									onkeypress="return(formataHora(this, event))" title="Hora">
									<f:convertDateTime pattern="HH:mm" type="time"  />
								</h:inputText> (HH:mm)
							</td>
						</tr>
						<tr>
							<th nowrap="nowrap" class="obrigatorio">Grande Área:</th>
							<td colspan="2">
								<a4j:region>
									<h:selectOneMenu value="#{bancaDefesaMBean.obj.grandeArea.id}" valueChangeListener="#{bancaDefesaMBean.carregaAreas}"
										id="grandeArea" style="width: 300px" immediate="true">
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
							<th class="obrigatorio">Área:</th>
							<td colspan="2">
								<a4j:region>
									<h:selectOneMenu value="#{bancaDefesaMBean.obj.area.id}" valueChangeListener="#{bancaDefesaMBean.carregaSubAreas}"
										id="area" style="width: 300px"
										 immediate="true">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{bancaDefesaMBean.areas}" />
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
								<h:selectOneMenu value="#{bancaDefesaMBean.obj.subArea.id}" id="subArea" style="width: 300px" immediate="true">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{bancaDefesaMBean.subAreas}" id="ItensSubAreas"/>
								</h:selectOneMenu>
							</td>
						</tr>	
						<tr>
							<th valign="center">Resumo:<span class="required">&nbsp;</span></th>
							<td colspan="2">
								<h:inputTextarea rows="10" id="dadosResumo" cols="80" value="#{bancaDefesaMBean.obj.resumo}" />
							</td>
						</tr>
						<tr>
							<th valign="center">Palavras Chave:<span class="required">&nbsp;</span></th>
							<td colspan="2">
								<h:inputTextarea id="palavrasChave" rows="10" cols="80" value="#{bancaDefesaMBean.obj.palavrasChave}" onkeyup="this.value = this.value.substring(0, 180);"/>
							</td>
						</tr>											
						<tr>
							<th>Observações:</th>
							<td colspan="2">
								<h:inputTextarea rows="10" id="observacao" cols="80" value="#{bancaDefesaMBean.obj.observacao}" />
							</td>
						</tr>
					</table>
				
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" id="cancelamento" action="#{bancaDefesaMBean.cancelar}"  onclick="#{confirm}"/>
						<h:commandButton id="proximoPasso" value="Próximo Passo >> " action="#{bancaDefesaMBean.submeterDadosGerais}" />
					</td>
				</tr>
			</tfoot>
		</table>		
	
	</h:form>
	
	<center>	
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
