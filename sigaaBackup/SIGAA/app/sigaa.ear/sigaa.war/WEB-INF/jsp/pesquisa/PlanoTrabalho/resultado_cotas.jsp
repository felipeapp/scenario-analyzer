<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>

	#cotasDocente {
		background: #F3F3F3;
		margin: 0 100px 15px;
		padding: 5px;
	}

	#cotasDocente h3 {
		text-align: center;
		font-variant: small-caps;
		letter-spacing: 1px;
		border-bottom: 1px solid #AAA;
		margin: 8px 30px 3px;
	}

	#cotasDocente ul {
		margin: 10px;
	}

	#cotasDocente p {
		margin: 10px;
		font-style: italic;
		text-align: center;
	}

	#cotasDocente ul table{
		margin: 5px 20px 10px;
		font-variant: small-caps;
		width: 60%;
	}

	#cotasDocente ul table th{
		text-align: left;
	}

	#cotasDocente ul table td{
		text-align: right;
	}

	#cotasDocente ul table tr.separator th, #cotasDocente ul table tr.separator td{
		border-bottom: 1px solid #DDD;
	}
</style>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Resultado da Distribuição de Cotas </h2>

<div id="cotasDocente">
	<c:if test="${not empty cotasDocente}">
	<h3> Cotas para o ano corrente </h3>
	<ul>
		<c:forEach var="cotaDocente" items="${ cotasDocente }">
				<li> <strong> Cota ${cotaDocente.edital.cota } </strong>
					<c:choose>
						<c:when test="${cotaDocente.edital.resultadoDivulgado}">
							<table>
								<tr>
									<th> Meu IPI </th>
									<td> ${cotaDocente.emissaoRelatorio.ipi}</td>
								</tr>
								<tr>
									<th> IPI médio do centro </th>
									<td> ${mediasDocente[cotaDocente.id]}</td>
								</tr>
								<tr>
									<th> Média dos meus projetos </th>
									<td> 
										<ufrn:format type="valor" name="cotaDocente" property="mediaProjetos" />
									</td>
								</tr>
								<tr class="separator">
									<th> Meu FPPI </th>
									<td> <ufrn:format type="valor" name="cotaDocente" property="fppi" /> </td>
								</tr>
								<tr class="separator">
									<th> <strong> Meu IFC </strong> </th>
									<td> <strong> <ufrn:format type="valor" name="cotaDocente" property="ifc" /> </strong> </td>
								</tr>
								<c:forEach var="cota" items="${cotaDocente.cotas}">
									<tr>
										<th> Bolsas ${cota.tipoBolsa.descricaoResumida} concedidas </th>
										<td> <b>${ cota.quantidade }</b> </td>
									</tr>
								</c:forEach>
							</table>
						</c:when>
						<c:otherwise>
							<p> O resultado da distribuição ainda não foi divulgado. </p>
						</c:otherwise>
					</c:choose>
				</li>
		</c:forEach>
	</ul>
	</c:if>

	<c:if test="${ empty cotasDocente }">
		<p> Você não foi contemplado com cotas de bolsa neste ano </p>
	</c:if>
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>