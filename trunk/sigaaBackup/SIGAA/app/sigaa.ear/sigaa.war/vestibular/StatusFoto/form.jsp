<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Status de Fotos</h2>

	<div class="descricaoOperacao">
		Cadastre um status para a foto do candidato.<br/>
		Caso a foto seja inválida, será enviado ao candidato, automaticamente, um e-mail solicitando-o alterar a foto utilizada no cadastro.
		Adicionalmente, será incluído no e-mail a recomendação, informada abaixo, de como corrigir o problema.
	</div>

	<h:form id="form">
		<a4j:keepAlive beanName="statusFotoMBean"></a4j:keepAlive>
		<table class="formulario" width="70%">
			<caption>Cadastro de Status de Foto 3x4</caption>
			<tr>
				<th width="40%">Foto Válida:</th>
				<td>
					<h:selectOneRadio value="#{statusFotoMBean.obj.valida}" id="fotoValidada" onclick="submit()" disabled="#{statusFotoMBean.readOnly}"> 
						<f:selectItems value="#{statusFotoMBean.simNao}"  />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th class="required">Descrição do Status da Foto:</th>
				<td>
					<h:inputText value="#{ statusFotoMBean.obj.descricao }" size="60" maxlength="60" readonly="#{statusFotoMBean.readOnly}" disabled="#{statusFotoMBean.readOnly}" id="descricao" />
				</td>
			</tr>
			<c:if test="${!statusFotoMBean.obj.valida}">
				<tr>
					<th>
						Recomendação a ser enviada ao candidato, caso a foto seja inválida:
					</th>
					<td>
						<h:inputTextarea value="#{ statusFotoMBean.obj.recomendacao }" rows="4" cols="60" readonly="#{statusFotoMBean.readOnly}" disabled="#{statusFotoMBean.readOnly}" id="recomendacao"/>
					</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden value="#{statusFotoMBean.obj.id}" /> 
						<h:commandButton value="#{statusFotoMBean.confirmButton}" action="#{statusFotoMBean.cadastrar}" id="btCadastar"/> 
						<c:if test="${statusFotoMBean.obj.id > 0}">
							<h:commandButton value="<< Voltar" action="#{statusFotoMBean.listar}" immediate="true" id="btVoltar" />
						</c:if> 
						<h:commandButton value="Cancelar" action="#{statusFotoMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
