<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2>Formação Acadêmica</h2>
	<h:messages showDetail="true"></h:messages>

    <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/atividades/FormacaoAcademica/lista.jsf" >Listar Formações Acadêmicas Cadastradas</a>
	 </div>
    </h:form>

 <!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form id="form">
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Formação Acadêmica</caption>
	<h:inputHidden value="#{formacaoAcademica.confirmButton}" />
	<h:inputHidden value="#{formacaoAcademica.obj.id}" />
	<tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

			<tr>
				<th class="required">Formação:</th>
				<td><h:selectOneMenu value="#{formacaoAcademica.obj.formacao.id}"
					disabled="#{formacaoAcademica.readOnly}" id="formacao"
					disabledClass="#{formacaoAcademica.disableClass}" onchange="javascript:atribuiObrigatorio();">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{formacaoTese.allCombo}" />
					</h:selectOneMenu></td>
			</tr>

			<tr>
				<th id="trTituloTrabalho">Título do Trabalho:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.titulo}"
					size="40" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Grau Acadêmico:</th>
				<td>
				<h:inputText size="40" maxlength="255" readonly="#{formacaoAcademica.readOnly}" value="#{formacaoAcademica.obj.grau}" id="grau" />
				</td>
			</tr>
			<tr>
				<th class="required">Entidade/Instituição:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.entidade}"
					size="40" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="entidade" /></td>
			</tr>
			<tr>
				<th class="required">País:</th>

				<td><h:selectOneMenu value="#{formacaoAcademica.obj.pais.id}"
					disabled="#{formacaoAcademica.readOnly}"
					disabledClass="#{formacaoAcademica.disableClass}" id="pais">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{pais.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			
			<tr>
				<th id="trOrientador">Orientador:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.orientador}"
					size="40" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="orientador" /></td>
			</tr>

			<tr>
				<th>Palavras-Chave 1:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.palavraChave1}"
					size="40" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="palavraChave1" /></td>
			</tr>
			<tr>
				<th>Palavras-Chave 2:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.palavraChave2}"
					size="40" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="palavraChave2" /></td>
			</tr>
			<tr>
				<th>Palavras-Chave 3:</th>
				<td><h:inputText value="#{formacaoAcademica.obj.palavraChave3}"
					size="40" maxlength="255" readonly="#{formacaoAcademica.readOnly}"
					id="palavraChave3" /></td>
			</tr>

	   </table>
	  </td>

	 <!-- Coluna 2 -->
	 <td width="30%">
	  <table id="coluna2" >

			<tr>
				<th class="required">Data de Início:</th>
				<td><t:inputCalendar
							value="#{formacaoAcademica.obj.dataInicio}" size="10"
							maxlength="10" readonly="#{formacaoAcademica.readOnly}"
							id="dataInicio"
							renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this, event))"/></td>
			</tr>

			<tr>
				<th>Data do Fim:</th>
				<td><t:inputCalendar
				value="#{formacaoAcademica.obj.dataFim}" size="10"
							maxlength="10" readonly="#{formacaoAcademica.readOnly}"
							id="dataFim"
							renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this, event))"/></td>
			</tr>

			<tr>
				<th class="required">Área:</th>

				<td><h:selectOneMenu style="width: 250px;" value="#{formacaoAcademica.obj.area.id}"
					disabled="#{formacaoAcademica.readOnly}" disabledClass="#{formacaoAcademica.disableClass}"
					id="area" valueChangeListener="#{producao.changeArea}">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{area.allCombo}" />
					<a4j:support event="onchange"reRender="subArea"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu style="width: 250px;" value="#{formacaoAcademica.obj.subArea.id}"
					disabled="#{formacaoAcademica.readOnly}" disabledClass="#{formacaoAcademica.disableClass}"
					id="subArea" >
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{producao.subArea}"/>
				</h:selectOneMenu></td>
			</tr>
			<%--
			<tr>
				<th class="required">Especialidade:</th>

				<td><h:selectOneMenu style="width: 250px;"
					value="#{formacaoAcademica.obj.especialidade.id}"
					disabled="#{formacaoAcademica.readOnly}"
					disabledClass="#{formacaoAcademica.disableClass}"
					id="especialidade">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{producao.especialidades}" />
				</h:selectOneMenu></td>
			</tr>
			--%>

	   		<tr>
				<th>Informações Complementares:</th>
				<td>
					<h:inputTextarea cols="35" rows="3" value="#{formacaoAcademica.obj.informacoes}"
					 readonly="#{formacaoAcademica.readOnly}"
					id="informacoes"  />
				</td>
			</tr>


	   </table>
	  </td>

	 </tr>

		<!-- Botoes -->
		<tfoot>
			<tr>
				<td colspan=2>
					<h:commandButton value="#{formacaoAcademica.confirmButton}"	action="#{formacaoAcademica.cadastrar}" id="cadastrar"/>
					<h:commandButton value="Cancelar" action="#{formacaoAcademica.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
		<!-- Fim Botoes -->


	</table>
    </h:form>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->




<!-- ############################################################################ -->
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
	
	<script>
		function atribuiObrigatorio() {
			var select = $('form:formacao');
			var trTituloTrabalho = $('trTituloTrabalho');
			var trOrientador = $('trOrientador');
			
			
			if (select.value != 1){
				trTituloTrabalho.className='required';
				trOrientador.className='required';
			} else {
				trTituloTrabalho.className='';
				trOrientador.className='';
			}
		}
		
		atribuiObrigatorio();
		
	</script>	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
