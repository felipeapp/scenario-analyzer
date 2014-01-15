<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Graus Acadêmicos</h2>

<ufrn:subSistema teste="not graduacao">
	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{grauAcademico.listar}"/>
			</div>
			</h:form>
	</center>
</ufrn:subSistema>

	<table class=formulario width="75%">
		<h:form id="formulario">
			<caption class="listagem">Informe os dados do Grau Acadêmico</caption>
			<h:inputHidden value="#{grauAcademico.confirmButton}" />
			<h:inputHidden value="#{grauAcademico.obj.id}" />
			<tr>
				<th class="required">Descrição: </th>	
				<td>
					<h:inputText id="descricao" size="60" maxlength="80" readonly="#{grauAcademico.readOnly}" value="#{grauAcademico.obj.descricao}" />
				</td>
			</tr>
			<tr>
				<th>Título para o Gênero Masculino:</th>	
				<td>
					<h:inputText id="titulo" size="60" maxlength="100" readonly="#{grauAcademico.readOnly}" value="#{grauAcademico.obj.tituloMasculino}" />
					<ufrn:help>Título que o discente receberá após a conclusão do curso.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Título para o Gênero Feminino:</th>	
				<td>
					<h:inputText id="tituloFeminino" size="60" maxlength="100" readonly="#{grauAcademico.readOnly}" value="#{grauAcademico.obj.tituloFeminino}" />
					<ufrn:help>Título que a discente receberá após a conclusão do curso.</ufrn:help>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{grauAcademico.confirmButton}" id="btnCadastrar"
						action="#{grauAcademico.cadastrar}" /> <h:commandButton id="btnCancelar"
						value="Cancelar" onclick="#{confirm}" action="#{grauAcademico.cancelar}" /></td>
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