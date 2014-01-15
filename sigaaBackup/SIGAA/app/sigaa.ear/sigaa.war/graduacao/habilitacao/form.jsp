<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Habilitações de Cursos de Graduação</h2>

	<h:messages showDetail="true"></h:messages>
	<br>
	<table class="formulario" width="100%">
		<h:form id="formulario">
			<h:outputText value="#{habilitacaoGrad.create}" />
			<caption class="listagem">Cadastro de Habilitação</caption>
			<h:inputHidden value="#{habilitacaoGrad.confirmButton}" />
			<h:inputHidden value="#{habilitacaoGrad.obj.id}" />
			<tr>
				<th class="required" width="30%">Nome:</th>
				<td>
					<h:inputText value="#{habilitacaoGrad.obj.nome}" size="60" maxlength="255" id="nome"
					readonly="#{habilitacaoGrad.readOnly}" onkeyup="CAPS(this)" /> 
				</td>
			</tr>
			
			<tr>
				<th class="required">Curso:</th>
				<td width="70%">
					<h:selectOneMenu id="curso" value="#{habilitacaoGrad.obj.curso.id}"
						readonly="#{habilitacaoGrad.readOnly}" disabledClass="#{habilitacaoGrad.disableClass}">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<ufrn:subSistema teste="graduacao">
							<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
						</ufrn:subSistema>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th>Área Sesu:</th>
				<td><h:selectOneMenu id="areaSesu" value="#{habilitacaoGrad.obj.areaSesu.id}"
					readonly="#{habilitacaoGrad.readOnly}" disabledClass="#{habilitacaoGrad.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{areaSesu.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			
			<tr>
				<th>Língua Obrigatória no Vestibular:</th>
				<td><h:selectOneMenu id="linguaVestibular" value="#{habilitacaoGrad.obj.linguaObrigatoriaVestibular.id}"
					readonly="#{habilitacaoGrad.readOnly}" disabledClass="#{habilitacaoGrad.disableClass}">
					<f:selectItem itemValue="0" itemLabel="NÃO POSSUI OBRIGATORIEDADE" />
					<f:selectItems value="#{linguaEstrangeira.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<%-- 
			<tr>
				<th class="required">Código:</th>
				<td>
					<h:inputText id="codigo" value="#{habilitacaoGrad.obj.codigoIes}" size="10"
					maxlength="10" readonly="#{habilitacaoGrad.readOnly}" onkeyup="CAPS(this)"/> 
				</td>
			</tr>
			--%>
			<tr>
				<th>Código INEP:</th>
				<td>
					<h:inputText id="codigoInep" value="#{habilitacaoGrad.obj.codigoHabilitacaoInep}" size="10"
					maxlength="10" readonly="#{habilitacaoGrad.readOnly}" onkeyup="return formatarInteiro(this);" />
				</td>
			</tr>
			<%-- 
			<tr>
				<th>Opção Para Habilitação:</th>
				<td><h:selectBooleanCheckbox value="#{habilitacaoGrad.obj.opcaoParaHabilitacao}"
					readonly="#{habilitacaoGrad.readOnly}" styleClass="noborder" /> <ufrn:help
					img="/img/ajuda.gif">Para o caso do curso que permite ao aluno  ingressar no curso e poder optar  por
					uma das habilitações do curso em tempo posterior ao seu ingresso
				</ufrn:help></td>
			</tr>
			--%>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton id="cadastrar" value="#{habilitacaoGrad.confirmButton}" action="#{habilitacaoGrad.cadastrar}" /> 
						<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}"	action="#{habilitacaoGrad.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

	<script type="text/javascript">$('formulario:nome').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
