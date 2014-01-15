<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema/> > Contatos da Coordenação</h2>

	<h:form id="form">
		<h:inputHidden value="#{coordenacaoCurso.confirmButton}" />
		<h:inputHidden value="#{coordenacaoCurso.obj.id}" />

		<table class="formulario" width="70%">
			<caption class="formulario">Informações de Contato</caption>
			<tr>
				<th width="40%">Página oficial da coordenação:</th>
				<td>
					<h:inputText value="#{coordenacaoCurso.obj.paginaOficialCoordenacao}" size="60" maxlength="140"/>
				</td>
			</tr>
			<tr>
				<th width="20%" class="required">E-mail:</th>
				<td>
					<h:inputText value="#{coordenacaoCurso.obj.emailContato}" size="60" maxlength="45"/>
				</td>
			</tr>
			<tr>
				<th width="20%" class="required">Telefone/ramal 1:</th>
				<td>
					<h:inputText value="#{coordenacaoCurso.obj.telefoneContato1}" size="14" maxlength="14" /> /
					<h:inputText value="#{coordenacaoCurso.obj.ramalTelefone1}" size="5" maxlength="5" onkeyup="return formatarInteiro(this);"/> (ramal)
				</td>
			</tr>
			<tr>
				<th width="20%">Telefone/ramal 2:</th>
				<td>
					<h:inputText value="#{coordenacaoCurso.obj.telefoneContato2}" size="14" maxlength="14" /> /
					<h:inputText value="#{coordenacaoCurso.obj.ramalTelefone2}" size="5" maxlength="5" onkeyup="return formatarInteiro(this);"/> (ramal)
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar" action="#{coordenacaoCurso.gravarContatos}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{coordenacaoCurso.cancelar}" />
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

	<script type="text/javascript">$('form:nomeServidor').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
