<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="serie"/>
<h2> <ufrn:subSistema /> &gt; ${serie.obj.id > 0 ? 'Alterar' : 'Cadastrar'} Série</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 90%">
	  <caption>Dados da Série</caption>
		<h:inputHidden value="#{serie.obj.id}" />
		<tbody>
			<tr>
				<th class="obrigatorio" width="50%"> Número:</th>
				<td><h:inputText value="#{serie.obj.numero}" size="2" maxlength="1" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{serie.obj.descricao}" size="10" maxlength="20" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
			<th class="obrigatorio">Curso:</th>
				<td>
					<h:selectOneMenu value="#{ serie.obj.cursoMedio.id }" style="width: 70%;" id="selectCurso">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ cursoMedio.allCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
			<th> Ativo: </th>
				<td>
					<h:selectBooleanCheckbox id="checkAtivo" value="#{serie.obj.ativo}" styleClass="noborder" disabled="#{serie.obj.id == 0}"/> 
				</td>
			</tr>
		</tbody>
		<tfoot>
		   <tr>
				<td colspan="2">
					<h:commandButton value="#{serie.confirmButton}" action="#{serie.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{serie.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   </tr>
		</tfoot>
	</table>
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>