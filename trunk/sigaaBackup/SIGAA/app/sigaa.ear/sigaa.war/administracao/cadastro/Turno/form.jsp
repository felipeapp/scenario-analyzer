<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Turno</h2>

<c:if test="${!turno.subSistemaGraduacao}">
	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{turno.listar}"/>
			</div>
			</h:form>
	</center>
</c:if>

	<table id="form" class=formulario width="70%">
		<h:form>
			<caption class="listagem">Cadastro de Turno</caption>
			<h:inputHidden value="#{turno.confirmButton}" />
			<h:inputHidden value="#{turno.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText id="descricao" size="60" maxlength="255"
					readonly="#{turno.readOnly}" value="#{turno.obj.descricao}" />
				</td>
			</tr>

			<tr>
				<th class="required">Sigla:</th>
				<td><h:inputText id="sigla" size="4" maxlength="4"
					readonly="#{turno.readOnly}" value="#{turno.obj.sigla}" />
				</td>
			</tr>
			
			<tr>
				<th class="required">Ativo:</th>
				<td>
				<h:selectBooleanCheckbox id="ativo" value="#{turno.obj.ativo}" readonly="#{turno.readOnly}" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{turno.confirmButton}" id="btnCadastrar"
						action="#{turno.cadastrar}" /> <h:commandButton value="Cancelar" onclick="#{confirm}" id="btnCancelar"
						action="#{turno.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>

 <br />
 <center>
 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>