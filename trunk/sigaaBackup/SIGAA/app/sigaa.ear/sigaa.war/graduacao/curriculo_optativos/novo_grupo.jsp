
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Grupo de Optativas &gt; Criar Novo Grupo </h2>

<f:view>

<h:form id="form">

<div class="descricaoOperacao">
	Digite a descrição e a carga horária mínima do grupo. Em seguida, selecione no grupo da esquerda 
	os componentes curriculares que farão parte do grupo e os copie para o grupo da direita, através
	do botão "Copiar".
</div>

<table class="formulario" width="100%">
	<caption>Dados do Grupo</caption>
	<tr>
		<th width="160">Componente Curricular:</th>
		<td>
			<h:selectOneMenu value="#{ grupoOptativasMBean.obj.componente.id }">
				<f:selectItem itemLabel="--SELECIONE--" itemValue="0"/>
				<f:selectItems value="#{ grupoOptativasMBean.componentes }"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th>Descrição: <span class="required">&nbsp;</span></th>
		<td><h:inputText id="descricao" value="#{grupoOptativasMBean.obj.descricao}" maxlength="80" size="85" /></td>
	</tr>
	<tr>
		<th>CH Mínima: <span class="required">&nbsp;</span></th>
		<td><h:inputText id="chMinima" title="Carga Horária Mínima" value="#{grupoOptativasMBean.obj.chMinima}" maxlength="3" size="3" onkeyup="return formatarInteiro(this);"/></td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
			<caption>Componentes do Grupo</caption>
			<tr><td>
			<rich:pickList id="componentes" value="#{ grupoOptativasMBean.componentesEscolhidos }" sourceListWidth="295" targetListWidth="295" listsHeight="200" 
				converter="#{ curriculoComponenteConverter }" copyAllControlLabel="Copiar Tudo" copyControlLabel="Copiar" 
				removeAllControlLabel="Remover Tudo" removeControlLabel="Remover">
				<f:selectItems value="#{ grupoOptativasMBean.componentesOptativos }"/>
			</rich:pickList>			
			</td></tr>
			</table>

		</td>
	</tr>
	<tfoot>
	<tr><td colspan="2">
		<h:commandButton value="#{ grupoOptativasMBean.obj.id == 0 ? 'Cadastrar' : 'Alterar' }" action="#{ grupoOptativasMBean.cadastrar }" id="btnCadastrar"/>&nbsp;
		<h:commandButton value="<< Voltar" action="#{ grupoOptativasMBean.telaGrupos }" id="btnVoltar"/>
		<h:commandButton value="Cancelar" action="#{ grupoOptativasMBean.cancelar }" onclick="if (!confirm('Deseja realmente cancelar a operação?')) { return false; }" id="btnCancelar"/>
	</td></tr>
	</tfoot>
</table>

<br />
<center><img src="${ctx}/img/required.gif" style="vertical-align: middle;" /> 
<span class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
<br />
		
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>