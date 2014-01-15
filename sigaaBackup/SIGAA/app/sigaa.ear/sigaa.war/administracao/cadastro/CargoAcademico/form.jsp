<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Cargo Acadêmico</h2>


	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{cargoAcademico.listar}"/>
			</div>
			</h:form>
	</center>


<table class=formulario width="45%">
		<h:form>
			<caption class="listagem">Cadastro de Cargo Acadêmico</caption>
			<h:inputHidden value="#{cargoAcademico.confirmButton}" />
			<h:inputHidden value="#{cargoAcademico.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{cargoAcademico.obj.descricao}" size="38" maxlength="38" 
				readonly="#{areaConhecimentoUnesco.readOnly}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{cargoAcademico.confirmButton}"
						action="#{cargoAcademico.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{cargoAcademico.cancelar}" /></td>
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