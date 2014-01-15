<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Instituições de Ensino</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{instituicoesEnsino.listar}"/>
			</div>
			</h:form>
	</center>

	<table class="formulario">
		<h:form>
			<caption class="listagem">Cadastro de Instituições de Ensino</caption>
			<h:inputHidden value="#{instituicoesEnsino.confirmButton}" />
			<h:inputHidden value="#{instituicoesEnsino.obj.id}" />
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText value="#{instituicoesEnsino.obj.nome}" size="60"
					maxlength="120" readonly="#{instituicoesEnsino.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Sigla:</th>
				<td><h:inputText value="#{instituicoesEnsino.obj.sigla}" size="12"
					maxlength="12" readonly="#{instituicoesEnsino.readOnly}" /></td>
			</tr>
			<tr>
				<th>Cnpj:</th>
				<td><h:inputText value="#{instituicoesEnsino.obj.cnpj}" size="20"
					maxlength="14" readonly="#{instituicoesEnsino.readOnly}" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<th class="required">País:</th>
				<td><h:selectOneMenu value="#{instituicoesEnsino.obj.pais.id}" readonly="#{instituicoesEnsino.readOnly}">
						<f:selectItems value="#{pais.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Tipo de Instituição:</th>
				<td><h:selectOneMenu value="#{instituicoesEnsino.obj.tipo.id}" readonly="#{instituicoesEnsino.readOnly}">
						<f:selectItems value="#{tipoInstituicao.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>


			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{instituicoesEnsino.confirmButton}"
						action="#{instituicoesEnsino.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{instituicoesEnsino.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>

 <br />
 <center>
 <h:graphicImage url="/img/required.gif"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>