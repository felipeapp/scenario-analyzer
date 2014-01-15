<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

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
	<h:outputText value="#{observacaoDiscente.create}"/>

	

	<h2> <ufrn:subSistema /> > Cadastro de Observação de Discente </h2>

	<c:set var="discente" value="#{observacaoDiscente.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<div id="abas-observacoes">
		<div id="nova-observacao">
			<h:form prependId="false" id="form1">
				<h:inputHidden value="#{observacaoDiscente.obj.discente.id}" id="id_discente"/>
				<h:inputHidden value="#{observacaoDiscente.obj.observacaoAnterior.id}" id="id_obs_anterior"/>
				<div id="texto-observacao">
					<h:inputTextarea id="observacao" value="#{observacaoDiscente.obj.observacao}" style="width: 99%" rows="8"/>
				</div>
				<div class="botoes-acao" style="background: #FFFFFF !important;">
					<h:commandButton value="Confirmar" action="#{observacaoDiscente.cadastrar}" id="confirmar" />
				</div>
			</h:form>
		</div>

		<div id="observacoes">
			<c:set var="observacoes" value="${ observacaoDiscente.observacoesDiscente }"/>
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
										<h:inputHidden value="#{ observacaoDiscente.obj.discente.id }" />
										<input type="hidden" value="${obs.id}" name="idObservacao" />
										<h:commandButton image="/img/alterar.gif"
											action="#{observacaoDiscente.alterar}"
											title="Alterar Observação" />
									</h:form>
								</td>
								<td width="3%">
									<h:form prependId="false">
										<h:inputHidden value="#{ observacaoDiscente.obj.discente.id }" />
										<input type="hidden" value="${obs.id}" name="idObservacao" />
										<h:commandButton image="/img/delete.gif"
											action="#{observacaoDiscente.remover}"
											title="Remover Observação" onclick="return confirm('Atenção! Deseja realmente remover esta observação?');"/>
									</h:form>
								</td>
							</tr>
						</table>
						<div class="usuario">
							cadastrada por ${obs.registro.usuario.pessoa.nome} (${obs.registro.usuario.login})
							em <ufrn:format type="dataHora" name="obs" property="data" />
							<c:if test="${obs.movimentacao != null}">
							<br>
							através de um ${obs.movimentacao.tipoMovimentacaoAluno.descricao}
							referente a ${obs.movimentacao.anoReferencia}.${obs.movimentacao.periodoReferencia}
							</c:if>
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
			<h:commandButton value="<< Selecionar Outro Discente" action="#{observacaoDiscente.voltar}" id="selecionar_outro_discente"/>
			<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{observacaoDiscente.cancelar}" id="cancelar"/>
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