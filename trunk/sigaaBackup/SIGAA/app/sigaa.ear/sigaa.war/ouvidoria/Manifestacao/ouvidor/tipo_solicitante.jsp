<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="manifestacaoOuvidoria" />

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Cadastro de Manifesta��o
	</h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>Caro usu�rio,</p>
		<p>Para iniciar o cadastro da manifesta��o, escolha a origem do solicitante e, se necess�rio, sua categoria no formul�rio abaixo.</p>
	</div>

	<h:form id="form">
		<table class="formulario" width="70%">
			<caption>Informa��es Sobre o Manifestante</caption>
			<tbody>
			<tr>
				<th class="required">Origem do Manifestante:</th>
				<td>
					<h:selectOneMenu id="origem" value="#{manifestacaoOuvidoria.origemManifestante }">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{manifestacaoOuvidoria.allOrigensManifesanteCombo }" />
						<a4j:support event="onchange" reRender="form" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required"><h:outputText id="labelCategoria" value="Categoria:" rendered="#{manifestacaoOuvidoria.comunidadeInterna }"></h:outputText></th>
				<td>
					<h:selectOneMenu id="categoria" value="#{manifestacaoOuvidoria.obj.interessadoManifestacao.categoriaSolicitante.id }" rendered="#{manifestacaoOuvidoria.comunidadeInterna }">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{manifestacaoOuvidoria.allCategoriasSolicitanteInternoCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			</tbody>				
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btn_proximo" value="Pr�ximo Passo >>" action="#{manifestacaoOuvidoria.submeterTipoSolicitante }" />
						<h:commandButton id="btn_cancelar" value="Cancelar"  action="#{manifestacaoOuvidoria.cancelar }" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>
	<br>
	</center>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>