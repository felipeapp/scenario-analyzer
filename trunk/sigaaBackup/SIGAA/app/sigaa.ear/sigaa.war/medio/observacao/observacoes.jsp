<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="observacaoDiscenteSerieMBean"/>
<style>
#observacoes, #nova-observacao {
	border: 1px solid #6593CF;
	border-width: 0 1px 1px 1px;
	padding: 1px;
	position: relative;
}

#texto-observacao {
	padding: 5px 15px;
}
.botoes-acao {
	background: #C4D2EB;
	text-align: center;
	padding: 4px 0;
}

#abas-observacoes .aviso{
	text-align: center;
	padding: 20px 0;
	font-style: italic;
}

#observacoes p {
	margin: 5px 3px 2px;
	padding: 5px;
	text-indent: 15px;
}

#observacoes .usuario {
	text-align: right;
	margin: 0 5px 15px 5px;
	font-size: 0.9em;
	color: #555;
}

#observacoes .remocao {
	float: right;
	width: 50px;
}

#observacoes table.observacao {
	width: 98%;
	margin: 2px auto 2px;
	border-bottom: 1px solid #C4D2EB;
	background: #EFF3FA;
}

</style>

<f:view>
	<h:outputText value="#{observacaoDiscenteSerieMBean.create}"/>

	

	<h2> <ufrn:subSistema /> > Editar Observações em Série do Discente </h2>

	<c:set var="discente" value="#{observacaoDiscenteSerieMBean.discente}"></c:set>
	<%@include file="/medio/discente/info_discente.jsp"%>	
	
	<table class="visualizacao" style="width: 90%">
		<caption>Dados da Série</caption>
		<tr>
			<th>Série/Turma:</th>
			<td>${observacaoDiscenteSerieMBean.obj.matricula.turmaSerie.descricaoSerieTurma}</td>
		</tr>
		<tr>
			<th>Situação:</th>
			<td>${observacaoDiscenteSerieMBean.obj.matricula.descricaoSituacao}</td>
		</tr>
	</table>	
	<br/>

	<div id="abas-observacoes">
		<div id="nova-observacao">
			<h:form prependId="false" id="form1">
				<h:inputHidden value="#{observacaoDiscenteSerieMBean.discente.id}" id="id_discente"/>
				<div id="texto-observacao">
					<h:inputTextarea id="observacao" value="#{observacaoDiscenteSerieMBean.obj.observacao}" style="width: 99%" rows="8"/>
				</div>
				<div class="botoes-acao" style="background: #FFFFFF !important;">
					<h:commandButton value="Confirmar" action="#{observacaoDiscenteSerieMBean.cadastrar}" id="confirmar" />
				</div>
			</h:form>
		</div>

		<div id="observacoes">
			<c:set var="observacoes" value="${ observacaoDiscenteSerieMBean.observacoesDiscente }"/>
			<c:choose>
				<c:when test="${not empty observacoes}">
					<c:forEach var="obs" items="${observacoes}">
						<table class="observacao">
							<tr>
								<td>
									<p>
										${obs.observacao}
									</p>
								</td>
								<td width="3%">
									<h:form prependId="false">
										<h:inputHidden value="#{ observacaoDiscenteSerieMBean.discente.id }" id="idDiscenteAlterar"/>
										<input type="hidden" value="${obs.id}" name="idObservacao" id="idObservacaoAlterar"/>
										<h:commandButton image="/img/alterar.gif"
											action="#{observacaoDiscenteSerieMBean.alterar}"
											title="Alterar Observação" id="alterar"/>
									</h:form>
								</td>
								<td width="3%">
									<h:form prependId="false">
										<h:inputHidden value="#{ observacaoDiscenteSerieMBean.discente.id }" id="idDiscenteRemover"/>
										<input type="hidden" value="${obs.id}" name="idObservacao" id="idObservacaoRemover"/>
										<h:commandButton image="/img/delete.gif"
											action="#{observacaoDiscenteSerieMBean.remover}" id="remover"
											title="Remover Observação" onclick="return confirm('Atenção! Deseja realmente remover esta observação?');"/>
									</h:form>
								</td>
							</tr>
						</table>
						<div class="usuario">
							cadastrada por ${obs.registro.usuario.pessoa.nome} (${obs.registro.usuario.login})
							em <ufrn:format type="dataHora" name="obs" property="data" />
						</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="aviso">
						Não há observações cadastradas para este discente.
					</div>
				</c:otherwise>
			</c:choose>


		</div>
	</div>
	<div class="botoes-acao">
		<h:form prependId="false">
			<h:commandButton value="<< Selecionar Outro Discente" action="#{observacaoDiscenteSerieMBean.buscarDiscente}" id="selecionar_outro_discente"/>
			<h:commandButton value="<< Selecionar Outra Matrícula" action="#{observacaoDiscenteSerieMBean.exibirMatriculas}" id="selecionar_outro_matricula" rendered="#{fn:length(observacaoDiscenteSerieMBean.matriculasSerie) > 1}"/>
			<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{observacaoDiscenteSerieMBean.cancelar}" id="cancelar"/>
		</h:form>
	</div>	

	<script>
	var AbasObservacoes = {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('abas-observacoes');
	        abas.addTab('nova-observacao', "Inserir Nova Observação");
	        abas.addTab('observacoes', "Observações Cadastradas");
	        abas.activate('observacoes');
	        <c:if test="${not empty alteracao}">
		        abas.activate('nova-observacao');
	        </c:if>
	    }
	};
	YAHOO.ext.EventManager.onDocumentReady(AbasObservacoes.init, AbasObservacoes, true);
	</script>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>