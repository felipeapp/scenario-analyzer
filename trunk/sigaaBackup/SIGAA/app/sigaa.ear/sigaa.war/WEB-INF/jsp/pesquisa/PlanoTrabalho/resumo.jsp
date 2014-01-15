<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:steps /> > Plano de Trabalho </h2>

<style>
	.formulario p {
		padding: 2px 8px 10px;
		line-height: 1.2em;
	}
</style>

<html:form action="/pesquisa/planoTrabalho/wizard" method="post" styleId="formPlanoTrabalho">

<c:set var="plano" value="${formPlanoTrabalho.obj}" />
<h3 class="tituloTabela"> Plano de Trabalho </h3>
<table class="formulario" width="100%">
	<tbody>
		<tr>
			<th width="20%"> <b>Projeto de Pesquisa:<b/> </th>
			<td> 
				<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${formPlanoTrabalho.obj.projetoPesquisa.id}">
					${formPlanoTrabalho.obj.projetoPesquisa.codigo} - ${formPlanoTrabalho.obj.projetoPesquisa.titulo} 
				</html:link>
			</td>
		</tr>
		<c:choose>
		  <c:when test="${empty formPlanoTrabalho.obj.externo.pessoa.nome}">
			<tr>
				<th><b>Orientador:</b></th>
				<td> ${formPlanoTrabalho.obj.orientador.pessoa.nome}</td>
			</tr>
			<tr>
				<th><b>Centro:</b></th>
				<td> ${formPlanoTrabalho.obj.orientador.unidade.gestora.nome}</td>
			</tr>
			<tr>
				<th><b>Departamento:</b></th>
				<td> ${formPlanoTrabalho.obj.orientador.unidade.nome}</td>
			</tr>
		  </c:when>
		  <c:otherwise>
			<tr>
				<th><b>Orientador Externo:</b></th>
				<td> ${formPlanoTrabalho.obj.externo.pessoa.nome}</td>
			</tr>
			<tr>
				<th><b>Centro:</b></th>
				<td> ${formPlanoTrabalho.obj.externo.unidade.gestora.nome}</td>
			</tr>
			<tr>
				<th><b>Departamento:</b></th>
				<td> ${formPlanoTrabalho.obj.externo.unidade.nome}</td>
			</tr>
		  </c:otherwise>
		</c:choose>	
		<c:if test="${not empty formPlanoTrabalho.obj.membroProjetoDiscente}">
		<tr>
			<th> <b>Discente:</b>  </th>
			<td> 
				<c:choose>
					<c:when test="${!empty formPlanoTrabalho.obj.membroProjetoDiscente.discente and not formPlanoTrabalho.obj.membroProjetoDiscente.inativo}">
						${formPlanoTrabalho.obj.membroProjetoDiscente.discente.matricula } - ${formPlanoTrabalho.obj.membroProjetoDiscente.discente.pessoa.nome }
					</c:when>
					<c:otherwise>
						<i> Discente não definido</i>
					</c:otherwise>
				</c:choose>	
			</td>
		</tr>
		</c:if>
		<tr>
			<th> <b>Tipo de Bolsa:</b> </th>
			<td> ${formPlanoTrabalho.obj.tipoBolsaString}</td>
		</tr>
		<tr>
			<th> <b>Tipo de Bolsa Desejada:</b> </th>
			<td> ${formPlanoTrabalho.bolsaDesejada }</td>
		</tr>
		<c:if test="${formPlanoTrabalho.obj.status != 0}">
		<tr>
			<th> <b>Status do Plano:</b> </th>
			<td>
				${formPlanoTrabalho.obj.statusString}
			</td>
		</tr>
		</c:if>
		<c:choose>
			<c:when test="${formPlanoTrabalho.obj.tipoBolsa.vinculadoCota}">
				<tr>
					<th> <b>Cota:</b> </th>
					<td> ${formPlanoTrabalho.obj.cota}</td>
				</tr>
				<c:if test="${ not formPlanoTrabalho.cadastroVoluntario }">
					<tr>
						<th> <b>Edital:</b> </th>
						<td> ${formPlanoTrabalho.obj.edital.descricao}</td>
					</tr>
				</c:if>
			</c:when>
			<c:otherwise>
				<tr>
					<th> <b>Período:</b> </th>
					<td>
						<ufrn:format type="data" valor="${formPlanoTrabalho.obj.dataInicio}" />  a 
						<ufrn:format type="data" valor="${formPlanoTrabalho.obj.dataFim}" />
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty formPlanoTrabalho.obj.continuacao}">
			<tr>
				<th> <b>É continuidade de plano do ano anterior?</b> </th>
				<td> ${formPlanoTrabalho.obj.continuacao ? 'Sim' : 'Não'}</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" class="subFormulario" style="text-align: left">&nbsp;&nbsp;&nbsp;Corpo do Plano de Trabalho</td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Título</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="plano" property="titulo"/></p></td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Introdução e Justificativa</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="plano" property="introducaoJustificativa"/></p>
			<c:if test="${empty plano.introducaoJustificativa}">
				<p>--NÃO PREENCHIDO--</p>
			</c:if>
			</td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Objetivos</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="plano" property="objetivos"/></p></td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Metodologia</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="plano" property="metodologia"/></p></td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Referências</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="plano" property="referencias"/></p></td>
		</tr>

		<tr> <td colspan="2" style="margin:0; padding: 0;">
			<div style="overflow: auto; width: 980px">
			<table id="cronograma" class="subFormulario" width="100%">
			<caption style="text-align: left">Cronograma de Atividades</caption>
			<thead>
				<tr>
					<th width="30%" rowspan="2"> Atividade </th>
					<c:forEach items="${formPlanoTrabalho.telaCronograma.mesesAno}" var="ano">
					<th colspan="${fn:length(ano.value)}" style="text-align: center" class="inicioAno fimAno">
						${ano.key}
					</th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${formPlanoTrabalho.telaCronograma.mesesAno}" var="ano">
						<c:forEach items="${ano.value}" var="mes" varStatus="status">
						<c:set var="classeCabecalho" value=""/>
						<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
						<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>

						<th class="${classeCabecalho}" style="text-align: center"> ${mes}	</th>
						</c:forEach>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:set var="numeroAtividades" value="${fn:length(formPlanoTrabalho.telaCronograma.cronogramas)}" />
				<c:set var="valoresCheckboxes" value=",${fn:join(formPlanoTrabalho.telaCronograma.calendario, ',')}" />
				<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
				<tr>
					<th> ${formPlanoTrabalho.telaCronograma.atividade[statusAtividades.index-1]} </th>
					<c:forEach items="${formPlanoTrabalho.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
						<c:forEach items="${ano.value}" var="mes">
							<c:set var="valorCheckbox" value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
							<c:set var="classeCelula" value=""/>
							<c:if test="${ fn:contains(valoresCheckboxes, valorCheckbox) }">
								<c:set var="classeCelula" value="selecionado"/>
							</c:if>
							<td align="center" class="${classeCelula}" >
								&nbsp;
							</td>
						</c:forEach>
					</c:forEach>
				</tr>
				</c:forEach>
			</tbody>
			</table>
			 </div>
			</td>
		</tr>
		<c:if test="${ not empty formPlanoTrabalho.obj.membrosDiscentes }">
		<tr> <td colspan="2" style="margin:0; padding: 0;">
			<table id="cronograma" class="subFormulario" width="100%">
			<caption style="text-align: center"> Histórico de Bolsistas </caption>
			<thead>
				<tr>
					<th width="60%" rowspan="2"> Discente </th>
					<th> Data de Indicação </th>
					<th> Início </th>
					<th> Fim </th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="membro" items="${ formPlanoTrabalho.obj.membrosDiscentes }" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> ${membro.discente.matricula } - ${membro.discente.pessoa.nome } </td>
					<td> <ufrn:format type="datahorasec" name="membro" property="dataIndicacao"/> </td>
					<td> <ufrn:format type="data" name="membro" property="dataInicio"/> </td>
					<td> <ufrn:format type="data" name="membro" property="dataFim"/> </td>
				</tr>
				</c:forEach>
			</tbody>
			</table>
		</td>
		</tr>
			<c:if test="${not empty remove}">
			<tr>
				<td colspan="2">
					<p style="padding: 10px 100px; text-align: center; color: #F00; background-color: #FDFAF6;">
					<b>Atenção:</b> <br/>
					Ao remover este plano de trabalho, todo o histórico de participação dos alunos como bolsistas ou voluntários também será removido.
					</p>
				</td>
			</tr>
			</c:if>

		</c:if>

		<%-- Parecer --%>
		<c:if test="${plano.parecerConsultor != null}">
		<tr>
			<td colspan="2" class="subFormulario" style="text-align:left">
				&nbsp;&nbsp;&nbsp;Parecer do Consultor (emitido em <ufrn:format type="dataHora" name="plano" property="dataAvaliacao" />)
			</td>
		</tr>
		<tr>
			<td colspan="2" style="padding: 15px 25px;">
				<p> <ufrn:format type="texto" name="plano" property="parecerConsultor"/> </p>
			</td>
		</tr>
		</c:if>

		<%-- Histórico --%>
		<c:if test="${ not empty comprovante and not empty plano.historico }">
		<tr> <td colspan="2" style="margin:0; padding: 0;">
	    <table class="subFormulario" width="100%">
		<caption style="text-align:left">Histórico do Plano de Trabalho</caption>
	        <thead>
	        	<tr>
				    <th style="text-align: center"> Data/Hora </th>
				    <th style="text-align: left"> Situação </th>
				    <th style="text-align: left"> Tipo de Bolsa </th>
				    <th style="text-align: left"> Usuário </th>
		       </tr>
	        </thead>
	        <tbody>

	        <c:forEach items="${plano.historico}" var="historico_" varStatus="status">
	            <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td width="20%" align="center">
						<ufrn:format type="dataHora" name="historico_" property="data"/>
					</td>
					<td style="text-align: left">
						${ historico_.descricaoStatus }
					</td>
					<td style="text-align: left">
						${ historico_.tipoBolsa.descricaoResumida }
					</td>
					<td>
						<c:choose>
							<c:when test="${not historico_.avaliacao}">
								${ historico_.registroEntrada.usuario.pessoa.nome }  <i>(${ historico_.registroEntrada.usuario.login })</i>
							</c:when>
							<c:otherwise>
								<c:if test="${ not empty historico_.registroEntrada.usuario.consultor }">
									<i>(c${ historico_.registroEntrada.usuario.consultor.codigo })</i>
								</c:if>
								<c:if test="${ empty historico_.registroEntrada.usuario.consultor }">
									<i>GESTOR DE PESQUISA</i>
								</c:if>
							</c:otherwise>
						</c:choose>
					</td>
	            </tr>
	        </c:forEach>
	    </table>
	    </td></tr>
		</c:if>

	</tbody>
	<c:if test="${empty comprovante}">
	<tfoot>
		<tr>
			<td colspan="2">
			<c:choose>
				<c:when test="${empty remove}">
					<html:button dispatch="finalizar">Confirmar</html:button>
					<html:button view="cronograma">&lt;&lt; Cronograma</html:button>
					<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
				</c:when>
				<c:otherwise>
					<html:button dispatch="finalizar">Confirmar Remoção</html:button>
					<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
	</tfoot>
	</c:if>
</table>

</html:form>

<c:if test="${not empty comprovante}">
<br />
<div class="voltar" style="text-align: center;">

	<table class="listagem">	
		<tfoot>
			<tr>
				<td>
					<html:button onclick="javascript: history.go(-1);" >Voltar</html:button>
				</td>
			</tr>
		</tfoot>
	</table>
	

</div>
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>