<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Linhas de Pesquisa</h2>

	<h:form id="cadastroLinhaPesquisa">
		<h:inputHidden value="#{linhaPesquisa.confirmButton}" />
		<h:inputHidden value="#{linhaPesquisa.obj.id}" />

		<table class="formulario">
			<caption class="formulario">Dados da Linha de Pesquisa</caption>
			<tr>
				<th class="required">Programa:</th>
				<td>
					<h:selectOneMenu id="programa" value="#{linhaPesquisa.obj.programa.id}" onchange="submit()" disabled="#{linhaPesquisa.readOnly}"
							valueChangeListener="#{linhaPesquisa.carregarAreas}">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{unidade.allProgramaPosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>


			<tr>
				<th>Área de Concentração:</th>
				<td>
					<h:selectOneMenu id="area" value="#{linhaPesquisa.obj.area.id}" disabled="#{linhaPesquisa.readOnly}">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{linhaPesquisa.possiveisAreas}" />
					</h:selectOneMenu>
				</td>
			</tr>


			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText id="nome" value="#{linhaPesquisa.obj.denominacao}" size="70" maxlength="120" onkeyup="CAPS(this)" disabled="#{linhaPesquisa.readOnly}"
					readonly="#{linhaPesquisa.readOnly}" /></td>
			</tr>

			<tr>
				<th>Descrição:</th>
				<td>
				   <h:inputTextarea id="descricao" cols="68" rows="3" value="#{linhaPesquisa.obj.descricao}" />  
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{linhaPesquisa.confirmButton}" id="confirmar"	action="#{linhaPesquisa.cadastrar}" />
						<h:commandButton value="Cancelar" id="cancelar"	action="#{linhaPesquisa.cancelar}" onclick="#{confirm}"/>
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

	<script type="text/javascript">$('cadastroLinhaPesquisa:nome').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
